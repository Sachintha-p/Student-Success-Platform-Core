package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.BadRequestException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.module1.repository.GroupMemberRepository;
import com.sliit.studentplatform.module3.dto.request.TaskRequest;
import com.sliit.studentplatform.module3.dto.response.KanbanBoardResponse;
import com.sliit.studentplatform.module3.dto.response.TaskResponse;
import com.sliit.studentplatform.module3.dto.response.TaskSummaryResponse;
import com.sliit.studentplatform.module3.entity.Milestone;
import com.sliit.studentplatform.module3.entity.Project;
import com.sliit.studentplatform.module3.entity.Task;
import com.sliit.studentplatform.module3.enums.TaskPriority;
import com.sliit.studentplatform.module3.enums.TaskStatus;
import com.sliit.studentplatform.module3.repository.MilestoneRepository;
import com.sliit.studentplatform.module3.repository.ProjectRepository;
import com.sliit.studentplatform.module3.repository.TaskRepository;
import com.sliit.studentplatform.module3.service.interfaces.IMilestoneService;
import com.sliit.studentplatform.module3.service.interfaces.ITaskService;
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
public class TaskServiceImpl implements ITaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final IMilestoneService milestoneService;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest req, Long userId) {
        if (req.getProjectId() == null && req.getMilestoneId() == null) {
            throw new BadRequestException("Either projectId OR milestoneId must be provided");
        }

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .status(req.getStatus() != null ? req.getStatus() : TaskStatus.TODO)
                .priority(req.getPriority() != null ? req.getPriority() : TaskPriority.MEDIUM)
                .dueDate(req.getDueDate())
                .position(req.getPosition() != null ? req.getPosition() : 0)
                .creator(creator)
                .build();

        if (req.getProjectId() != null) {
            Project project = projectRepository.findById(req.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", req.getProjectId()));
            validateTeamMembership(project.getTeam().getId(), userId);
            task.setProject(project);
        }

        if (req.getMilestoneId() != null) {
            Milestone milestone = milestoneRepository.findById(req.getMilestoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", req.getMilestoneId()));
            validateTeamMembership(milestone.getProject().getTeam().getId(), userId);
            task.setMilestone(milestone);
            if (task.getProject() == null) {
                task.setProject(milestone.getProject());
            }
        }

        if (req.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(req.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", req.getAssignedToId()));
            task.setAssignedTo(assignedTo);
        }

        Task savedTask = taskRepository.save(task);
        if (savedTask.getMilestone() != null) {
            milestoneService.recalculateMilestoneProgress(savedTask.getMilestone().getId());
        }
        return mapToResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest req, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        validateTeamMembership(task.getProject().getTeam().getId(), userId);

        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        if (req.getStatus() != null) task.setStatus(req.getStatus());
        if (req.getPriority() != null) task.setPriority(req.getPriority());
        task.setDueDate(req.getDueDate());
        if (req.getPosition() != null) task.setPosition(req.getPosition());

        if (req.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(req.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", req.getAssignedToId()));
            task.setAssignedTo(assignedTo);
        } else {
            task.setAssignedTo(null);
        }

        Task savedTask = taskRepository.save(task);
        if (savedTask.getMilestone() != null) {
            milestoneService.recalculateMilestoneProgress(savedTask.getMilestone().getId());
        }
        return mapToResponse(savedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        validateTeamMembership(task.getProject().getTeam().getId(), userId);

        Long milestoneId = task.getMilestone() != null ? task.getMilestone().getId() : null;
        taskRepository.delete(task);
        if (milestoneId != null) {
            milestoneService.recalculateMilestoneProgress(milestoneId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectIdOrderByPositionAsc(projectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByMilestone(Long milestoneId) {
        return taskRepository.findByMilestoneIdOrderByPositionAsc(milestoneId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByUser(Long userId) {
        return taskRepository.findByAssignedToIdOrderByPositionAsc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatusOrderByPositionAsc(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(Long id, TaskStatus status, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        validateTeamMembership(task.getProject().getTeam().getId(), userId);
        task.setStatus(status);

        Task savedTask = taskRepository.save(task);
        if (savedTask.getMilestone() != null) {
            milestoneService.recalculateMilestoneProgress(savedTask.getMilestone().getId());
        }
        return mapToResponse(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskPosition(Long id, Integer position, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        validateTeamMembership(task.getProject().getTeam().getId(), userId);
        task.setPosition(position);

        return mapToResponse(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public KanbanBoardResponse getKanbanBoard(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectIdOrderByPositionAsc(projectId);

        return KanbanBoardResponse.builder()
                .todo(tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).map(this::mapToResponse).collect(Collectors.toList()))
                .inProgress(tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).map(this::mapToResponse).collect(Collectors.toList()))
                .done(tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).map(this::mapToResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public TaskResponse assignTask(Long id, Long assigneeId, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        validateTeamMembership(task.getProject().getTeam().getId(), userId);

        if (assigneeId != null) {
            validateTeamMembership(task.getProject().getTeam().getId(), assigneeId);
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", assigneeId));
            task.setAssignedTo(assignee);
        } else {
            task.setAssignedTo(null);
        }

        return mapToResponse(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getGroupTasks(Long groupId, TaskStatus status, TaskPriority priority, Long assignedToId, LocalDate dueBefore, LocalDate dueAfter) {
        // Simple implementation using stream filtering for now, could be optimized with Criteria API or Specification
        return taskRepository.findByGroupId(groupId).stream()
                .filter(t -> status == null || t.getStatus() == status)
                .filter(t -> priority == null || t.getPriority() == priority)
                .filter(t -> assignedToId == null || (t.getAssignedTo() != null && t.getAssignedTo().getId().equals(assignedToId)))
                .filter(t -> dueBefore == null || (t.getDueDate() != null && !t.getDueDate().isAfter(dueBefore)))
                .filter(t -> dueAfter == null || (t.getDueDate() != null && !t.getDueDate().isBefore(dueAfter)))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskSummaryResponse getGroupTaskSummary(Long groupId) {
        List<Object[]> statusCountsResult = taskRepository.countTasksByStatusForGroup(groupId);
        long totalTasks = taskRepository.countTotalTasksForGroup(groupId);

        java.util.Map<String, Long> statusCounts = new java.util.HashMap<>();
        // Initialize all statuses with 0
        for (TaskStatus status : TaskStatus.values()) {
            statusCounts.put(status.name(), 0L);
        }

        for (Object[] result : statusCountsResult) {
            TaskStatus status = (TaskStatus) result[0];
            Long count = (Long) result[1];
            statusCounts.put(status.name(), count);
        }

        return TaskSummaryResponse.builder()
                .statusCounts(statusCounts)
                .totalTasks(totalTasks)
                .build();
    }

    private void validateTeamMembership(Long teamId, Long userId) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(teamId, userId)) {
            throw new UnauthorizedException("Only team members can manage tasks");
        }
    }

    private TaskResponse mapToResponse(Task task) {
        LocalDate today = LocalDate.now();
        boolean isOverdue = task.getStatus() != TaskStatus.DONE && 
                           task.getDueDate() != null && 
                           task.getDueDate().isBefore(today);
        
        boolean isUpcoming = task.getStatus() != TaskStatus.DONE && 
                            task.getDueDate() != null && 
                            !task.getDueDate().isBefore(today) && 
                            task.getDueDate().isBefore(today.plusDays(3));

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .position(task.getPosition())
                .projectId(task.getProject() != null ? task.getProject().getId() : null)
                .milestoneId(task.getMilestone() != null ? task.getMilestone().getId() : null)
                .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .createdById(task.getCreator().getId())
                .isOverdue(isOverdue)
                .isUpcoming(isUpcoming)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
