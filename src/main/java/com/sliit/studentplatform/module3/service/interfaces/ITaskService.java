package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.module3.dto.request.TaskRequest;
import com.sliit.studentplatform.module3.dto.response.KanbanBoardResponse;
import com.sliit.studentplatform.module3.dto.response.TaskResponse;
import com.sliit.studentplatform.module3.dto.response.TaskSummaryResponse;
import com.sliit.studentplatform.module3.enums.TaskPriority;
import com.sliit.studentplatform.module3.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public interface ITaskService {
    TaskResponse createTask(TaskRequest req, Long userId);
    TaskResponse getTaskById(Long id);
    TaskResponse updateTask(Long id, TaskRequest req, Long userId);
    void deleteTask(Long id, Long userId);
    List<TaskResponse> getTasksByProject(Long projectId);
    List<TaskResponse> getTasksByMilestone(Long milestoneId);
    List<TaskResponse> getTasksByUser(Long userId);
    List<TaskResponse> getTasksByStatus(TaskStatus status);
    TaskResponse updateTaskStatus(Long id, TaskStatus status, Long userId);
    TaskResponse updateTaskPosition(Long id, Integer position, Long userId);
    KanbanBoardResponse getKanbanBoard(Long projectId);

    // New methods for Module 3 consistency
    TaskResponse assignTask(Long id, Long assigneeId, Long userId);
    List<TaskResponse> getGroupTasks(Long groupId, TaskStatus status, TaskPriority priority, Long assignedToId, LocalDate dueBefore, LocalDate dueAfter);
    TaskSummaryResponse getGroupTaskSummary(Long groupId);
}
