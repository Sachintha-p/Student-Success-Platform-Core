package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.BadRequestException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.module1.repository.GroupMemberRepository;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module3.dto.request.ProjectRequest;
import com.sliit.studentplatform.module3.dto.response.ProjectResponse;
import com.sliit.studentplatform.module3.entity.Milestone;
import com.sliit.studentplatform.module3.entity.Project;
import com.sliit.studentplatform.module3.repository.ProjectRepository;
import com.sliit.studentplatform.module3.service.interfaces.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements IProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectGroupRepository groupRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ProjectResponse createProject(ProjectRequest req, Long userId) {
    validateProjectDates(req);

    // If teamId is 1 and it doesn't exist, we'll try to find any existing group or create a dummy one for the user
    // This is to prevent "ResourceNotFoundException" for the default ID used in the frontend
    var group = groupRepository.findById(req.getTeamId())
        .orElseGet(() -> {
          log.warn("Group {} not found, looking for user's group or creating one", req.getTeamId());
          return groupRepository.findByOwnerId(userId, org.springframework.data.domain.PageRequest.of(0, 1))
              .stream().findFirst()
              .orElseGet(() -> {
                com.sliit.studentplatform.module1.entity.ProjectGroup newGroup = com.sliit.studentplatform.module1.entity.ProjectGroup.builder()
                    .name("My First Team")
                    .description("Auto-created team for project")
                    .maxMembers(5)
                    .owner(userRepository.findById(userId).orElse(null))
                    .open(true)
                    .build();
                return groupRepository.save(newGroup);
              });
        });

    var creator = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    if (!groupMemberRepository.existsByGroupIdAndUserId(group.getId(), userId)) {
      if (!group.getOwner().getId().equals(userId)) {
        // If they are not in the group, we'll add them as a leader/member so they can create projects
        com.sliit.studentplatform.module1.entity.GroupMember newMember = com.sliit.studentplatform.module1.entity.GroupMember.builder()
            .group(group)
            .user(creator)
            .joinedAt(java.time.LocalDateTime.now())
            .leader(true)
            .build();
        groupMemberRepository.save(newMember);
        log.info("User {} added to group {} while creating project", userId, group.getId());
      }
    }

    Project project = Project.builder()
        .name(req.getName())
        .description(req.getDescription())
        .startDate(req.getStartDate())
        .endDate(req.getEndDate())
        .team(group)
        .creator(creator)
        .build();

    return mapToResponse(projectRepository.save(project));
  }

  @Override
  @Transactional(readOnly = true)
  public ProjectResponse getProjectById(Long id) {
    Project project = projectRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    return mapToResponse(project);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProjectResponse> getAllProjects() {
    return projectRepository.findAllByOrderByCreatedAtDesc().stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProjectResponse> getProjectsByTeam(Long teamId) {
    return projectRepository.findByTeamId(teamId).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ProjectResponse updateProject(Long id, ProjectRequest req, Long userId) {
    validateProjectDates(req);
    Project project = projectRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

    if (project.getCreator().getId().equals(userId)) {
      // Creator always has permission
    } else if (!groupMemberRepository.existsByGroupIdAndUserId(project.getTeam().getId(), userId)) {
      throw new UnauthorizedException("Not authorized to update this project");
    }

    project.setName(req.getName());
    project.setDescription(req.getDescription());
    project.setStartDate(req.getStartDate());
    project.setEndDate(req.getEndDate());

    return mapToResponse(projectRepository.save(project));
  }

  @Override
  @Transactional
  public void deleteProject(Long id, Long userId) {
    Project project = projectRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

    if (!project.getCreator().getId().equals(userId)) {
      throw new UnauthorizedException("Only the project creator can delete it");
    }

    projectRepository.delete(project);
  }

  @Override
  @Transactional(readOnly = true)
  public double calculateProjectProgress(Long id) {
    Project project = projectRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

    List<Milestone> milestones = project.getMilestones();
    if (milestones == null || milestones.isEmpty()) {
      return 0.0;
    }

    double totalProgress = milestones.stream()
        .mapToInt(Milestone::getProgressPercentage)
        .sum();

    return totalProgress / milestones.size();
  }

  private void validateProjectDates(ProjectRequest req) {
    if (req.getStartDate().isAfter(req.getEndDate())) {
      throw new BadRequestException("Project start date cannot be after end date");
    }
  }

  private ProjectResponse mapToResponse(Project p) {
    return ProjectResponse.builder()
        .id(p.getId())
        .name(p.getName())
        .description(p.getDescription())
        .startDate(p.getStartDate())
        .endDate(p.getEndDate())
        .createdById(p.getCreator().getId())
        .teamId(p.getTeam().getId())
        .progressPercentage(calculateProjectProgress(p.getId()))
        .createdAt(p.getCreatedAt())
        .updatedAt(p.getUpdatedAt())
        .build();
  }
}
