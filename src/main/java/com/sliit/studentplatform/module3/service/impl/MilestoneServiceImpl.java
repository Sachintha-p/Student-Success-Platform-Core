package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.BadRequestException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.module1.repository.GroupMemberRepository;
import com.sliit.studentplatform.module3.dto.request.MilestoneRequest;
import com.sliit.studentplatform.module3.dto.response.AllProjectsMilestonesResponse;
import com.sliit.studentplatform.module3.dto.response.MilestoneProgressSummaryResponse;
import com.sliit.studentplatform.module3.dto.response.MilestoneResponse;
import com.sliit.studentplatform.module3.dto.response.MilestoneTimelineResponse;
import com.sliit.studentplatform.module3.entity.Milestone;
import com.sliit.studentplatform.module3.entity.Project;
import com.sliit.studentplatform.module3.entity.Task;
import com.sliit.studentplatform.module3.enums.MilestoneStatus;
import com.sliit.studentplatform.module3.enums.TaskStatus;
import com.sliit.studentplatform.module3.repository.MilestoneRepository;
import com.sliit.studentplatform.module3.repository.ProjectRepository;
import com.sliit.studentplatform.module3.repository.TaskRepository;
import com.sliit.studentplatform.module3.service.interfaces.IMilestoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MilestoneServiceImpl implements IMilestoneService {

  private final MilestoneRepository milestoneRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final TaskRepository taskRepository;

  @Override
  @Transactional
  public MilestoneResponse createMilestone(MilestoneRequest req, Long userId) {
    Project project = projectRepository.findById(req.getProjectId())
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", req.getProjectId()));

    validateMilestoneData(req, project, userId);

    Milestone milestone = Milestone.builder()
        .project(project)
        .title(req.getTitle())
        .description(req.getDescription())
        .startDate(req.getStartDate())
        .dueDate(req.getDueDate())
        .status(req.getStatus() != null ? req.getStatus() : MilestoneStatus.NOT_STARTED)
        .progressPercentage(req.getProgressPercentage() != null ? req.getProgressPercentage() : 0)
        .assignedTo(req.getAssignedToId() != null ? 
            userRepository.findById(req.getAssignedToId()).orElse(null) : null)
        .build();

    return mapToResponse(milestoneRepository.save(milestone));
  }

  @Override
  @Transactional
  public MilestoneResponse updateMilestone(Long id, MilestoneRequest req, Long userId) {
    Milestone milestone = milestoneRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", id));

    validateMilestoneData(req, milestone.getProject(), userId);

    milestone.setTitle(req.getTitle());
    milestone.setDescription(req.getDescription());
    milestone.setStartDate(req.getStartDate());
    milestone.setDueDate(req.getDueDate());
    if (req.getStatus() != null) milestone.setStatus(req.getStatus());
    if (req.getProgressPercentage() != null) milestone.setProgressPercentage(req.getProgressPercentage());
    if (req.getAssignedToId() != null) {
      milestone.setAssignedTo(userRepository.findById(req.getAssignedToId()).orElse(null));
    }

    return mapToResponse(milestoneRepository.save(milestone));
  }

  @Override
  @Transactional
  public void deleteMilestone(Long id, Long userId) {
    Milestone milestone = milestoneRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", id));

    if (!groupMemberRepository.existsByGroupIdAndUserId(milestone.getProject().getTeam().getId(), userId)) {
      throw new UnauthorizedException("Only team members can delete milestones");
    }

    milestoneRepository.delete(milestone);
  }

  @Override
  @Transactional(readOnly = true)
  public MilestoneResponse getMilestoneById(Long id) {
    Milestone milestone = milestoneRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", id));
    return mapToResponse(milestone);
  }

  @Override
  @Transactional(readOnly = true)
  public List<MilestoneResponse> getMilestonesByProject(Long projectId) {
    return milestoneRepository.findByProjectIdOrderByStartDateAsc(projectId).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public MilestoneResponse updateProgress(Long id, int progressPercentage, Long userId) {
    Milestone milestone = milestoneRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", id));

    if (!groupMemberRepository.existsByGroupIdAndUserId(milestone.getProject().getTeam().getId(), userId)) {
      throw new UnauthorizedException("Only team members can update milestone progress");
    }

    if (progressPercentage < 0 || progressPercentage > 100) {
      throw new BadRequestException("Progress percentage must be between 0 and 100");
    }

    milestone.setProgressPercentage(progressPercentage);
    updateMilestoneStatusBasedOnProgress(milestone, progressPercentage);

    return mapToResponse(milestoneRepository.save(milestone));
  }

  @Override
  @Transactional
  public void recalculateMilestoneProgress(Long milestoneId) {
    Milestone milestone = milestoneRepository.findById(milestoneId)
        .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", milestoneId));

    List<Task> tasks = taskRepository.findByMilestoneIdOrderByPositionAsc(milestoneId);
    if (tasks.isEmpty()) return;

    long completedTasks = tasks.stream()
        .filter(t -> t.getStatus() == TaskStatus.DONE)
        .count();

    int progress = (int) ((completedTasks * 100.0) / tasks.size());
    milestone.setProgressPercentage(progress);
    updateMilestoneStatusBasedOnProgress(milestone, progress);

    milestoneRepository.save(milestone);
  }

  private void updateMilestoneStatusBasedOnProgress(Milestone milestone, int progress) {
    if (progress == 100) {
      milestone.setStatus(MilestoneStatus.COMPLETED);
    } else if (progress > 0) {
      milestone.setStatus(MilestoneStatus.IN_PROGRESS);
    } else {
      if (milestone.getDueDate().isBefore(LocalDate.now())) {
        milestone.setStatus(MilestoneStatus.NOT_STARTED); // Could also be OVERDUE logic if we add that status
      } else {
        milestone.setStatus(MilestoneStatus.NOT_STARTED);
      }
    }
  }

  @Override
  @Transactional(readOnly = true)
  public MilestoneTimelineResponse getTimeline(Long projectId) {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    List<MilestoneResponse> milestones = getMilestonesByProject(projectId);

    return MilestoneTimelineResponse.builder()
        .milestones(milestones)
        .projectName(project.getName())
        .projectStartDate(project.getStartDate())
        .projectEndDate(project.getEndDate())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public MilestoneProgressSummaryResponse getProgressSummary(Long projectId) {
    List<Milestone> milestones = milestoneRepository.findByProjectIdOrderByStartDateAsc(projectId);
    
    int total = milestones.size();
    if (total == 0) {
      return MilestoneProgressSummaryResponse.builder().build();
    }

    int completed = (int) milestones.stream().filter(m -> m.getStatus() == MilestoneStatus.COMPLETED).count();
    int overdue = (int) milestones.stream()
        .filter(m -> m.getStatus() != MilestoneStatus.COMPLETED && m.getDueDate().isBefore(LocalDate.now()))
        .count();
    int upcoming = (int) milestones.stream()
        .filter(m -> m.getStatus() != MilestoneStatus.COMPLETED && 
                     !m.getDueDate().isBefore(LocalDate.now()) && 
                     m.getDueDate().isBefore(LocalDate.now().plusDays(7)))
        .count();

    double avgProgress = milestones.stream().mapToInt(Milestone::getProgressPercentage).average().orElse(0.0);

    List<MilestoneResponse> upcomingDeadlines = milestones.stream()
        .filter(m -> m.getStatus() != MilestoneStatus.COMPLETED && !m.getDueDate().isBefore(LocalDate.now()))
        .sorted((a, b) -> a.getDueDate().compareTo(b.getDueDate()))
        .limit(5)
        .map(this::mapToResponse)
        .collect(Collectors.toList());

    return MilestoneProgressSummaryResponse.builder()
        .totalMilestones(total)
        .completedMilestones(completed)
        .overdueMilestones(overdue)
        .upcomingMilestones(upcoming)
        .overallProgressPercentage(Math.round(avgProgress * 100.0) / 100.0)
        .upcomingDeadlines(upcomingDeadlines)
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<MilestoneResponse> getUpcomingDeadlines(Long projectId, int days) {
    LocalDate futureDate = LocalDate.now().plusDays(days);
    return milestoneRepository.findByProjectIdAndStatusNotAndDueDateBetween(
        projectId, MilestoneStatus.COMPLETED, LocalDate.now(), futureDate).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public AllProjectsMilestonesResponse getAllProjectsTimeline() {
    List<Project> projects = projectRepository.findAllByOrderByCreatedAtDesc();
    
    List<MilestoneTimelineResponse> projectTimelines = projects.stream()
        .map(p -> MilestoneTimelineResponse.builder()
            .projectName(p.getName())
            .projectStartDate(p.getStartDate())
            .projectEndDate(p.getEndDate())
            .milestones(p.getMilestones().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()))
            .build())
        .collect(Collectors.toList());

    int totalMilestones = (int) projectTimelines.stream()
        .mapToLong(pt -> pt.getMilestones().size())
        .sum();

    return AllProjectsMilestonesResponse.builder()
        .projectTimelines(projectTimelines)
        .totalProjects(projects.size())
        .totalMilestones(totalMilestones)
        .build();
  }

  private void validateMilestoneData(MilestoneRequest req, Project project, Long userId) {
    if (!groupMemberRepository.existsByGroupIdAndUserId(project.getTeam().getId(), userId)) {
      throw new UnauthorizedException("Only team members can manage milestones");
    }

    if (req.getStartDate().isAfter(req.getDueDate())) {
      throw new BadRequestException("Milestone start date cannot be after due date");
    }

    if (req.getStartDate().isBefore(project.getStartDate()) || req.getDueDate().isAfter(project.getEndDate())) {
      throw new BadRequestException("Milestone dates must be within project duration (" + 
          project.getStartDate() + " to " + project.getEndDate() + ")");
    }
  }

  private MilestoneResponse mapToResponse(Milestone m) {
    boolean isOverdue = m.getStatus() != MilestoneStatus.COMPLETED && m.getDueDate().isBefore(LocalDate.now());
    boolean isUpcoming = m.getStatus() != MilestoneStatus.COMPLETED && 
                         !m.getDueDate().isBefore(LocalDate.now()) && 
                         m.getDueDate().isBefore(LocalDate.now().plusDays(7));

    return MilestoneResponse.builder()
        .id(m.getId())
        .projectId(m.getProject().getId())
        .title(m.getTitle())
        .description(m.getDescription())
        .startDate(m.getStartDate())
        .dueDate(m.getDueDate())
        .status(m.getStatus())
        .progressPercentage(m.getProgressPercentage())
        .assignedToId(m.getAssignedTo() != null ? m.getAssignedTo().getId() : null)
        .isOverdue(isOverdue)
        .isUpcoming(isUpcoming)
        .createdAt(m.getCreatedAt())
        .updatedAt(m.getUpdatedAt())
        .build();
  }
}
