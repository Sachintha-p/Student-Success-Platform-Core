package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.dto.request.CreateTaskRequest;
import com.sliit.studentplatform.module3.dto.response.TaskResponse;
import com.sliit.studentplatform.module3.service.interfaces.ITaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Milestone Tasks")
public class TaskController {
  private final ITaskService taskService;

  @PostMapping
  public ResponseEntity<ApiResponse<TaskResponse>> create(@Valid @RequestBody CreateTaskRequest req,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(taskService.createTask(req, user.getId()), "Task created"));
  }

  @GetMapping("/milestone/{milestoneId}")
  public ResponseEntity<ApiResponse<List<TaskResponse>>> getByMilestone(@PathVariable Long milestoneId) {
    return ResponseEntity.ok(ApiResponse.success(taskService.getTasksByMilestone(milestoneId), "Tasks retrieved"));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(@PathVariable Long id,
      @RequestParam String status, @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity
        .ok(ApiResponse.success(taskService.updateTaskStatus(id, status, user.getId()), "Status updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
    taskService.deleteTask(id, user.getId());
    return ResponseEntity.ok(ApiResponse.success("Task deleted"));
  }
}
