package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module3.dto.request.CreateTaskRequest;
import com.sliit.studentplatform.module3.dto.response.TaskResponse;
import com.sliit.studentplatform.module3.entity.MilestoneTask;
import com.sliit.studentplatform.module3.repository.MilestoneTaskRepository;
import com.sliit.studentplatform.module3.repository.ProjectMilestoneRepository;
import com.sliit.studentplatform.module3.service.interfaces.ITaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements ITaskService {

  private final MilestoneTaskRepository taskRepository;
  private final ProjectMilestoneRepository milestoneRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public TaskResponse createTask(CreateTaskRequest req, Long userId) {
    var milestone = milestoneRepository.findById(req.getMilestoneId())
        .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", req.getMilestoneId()));
    var assignee = req.getAssigneeId() != null
        ? userRepository.findById(req.getAssigneeId()).orElse(null)
        : null;
    var task = taskRepository.save(MilestoneTask.builder()
        .milestone(milestone).assignee(assignee).title(req.getTitle())
        .description(req.getDescription()).dueDate(req.getDueDate())
        .status("TODO").build());
    return mapToResponse(task);
  }

  @Override
  @Transactional(readOnly = true)
  public TaskResponse getTask(Long id) {
    return mapToResponse(taskRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskResponse> getTasksByMilestone(Long milestoneId) {
    return taskRepository.findByMilestoneId(milestoneId).stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public TaskResponse updateTaskStatus(Long id, String status, Long userId) {
    var t = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    t.setStatus(status);
    return mapToResponse(taskRepository.save(t));
  }

  @Override
  @Transactional
  public void deleteTask(Long id, Long userId) {
    taskRepository.deleteById(id);
  }

  private TaskResponse mapToResponse(MilestoneTask t) {
    return TaskResponse.builder().id(t.getId()).milestoneId(t.getMilestone().getId())
        .assigneeId(t.getAssignee() != null ? t.getAssignee().getId() : null)
        .assigneeName(t.getAssignee() != null ? t.getAssignee().getFullName() : null)
        .title(t.getTitle()).description(t.getDescription()).dueDate(t.getDueDate())
        .status(t.getStatus()).priority(t.getPriority() != null ? t.getPriority().name() : null).build();
  }
}
