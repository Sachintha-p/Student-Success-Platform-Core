package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.module3.dto.request.CreateTaskRequest;
import com.sliit.studentplatform.module3.dto.response.TaskResponse;
import java.util.List;

public interface ITaskService {
  TaskResponse createTask(CreateTaskRequest request, Long userId);

  TaskResponse getTask(Long id);

  List<TaskResponse> getTasksByMilestone(Long milestoneId);

  TaskResponse updateTaskStatus(Long id, String status, Long userId);

  void deleteTask(Long id, Long userId);
}
