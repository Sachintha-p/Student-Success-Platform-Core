package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.dto.request.TaskRequest;
import com.sliit.studentplatform.module3.dto.response.KanbanBoardResponse;
import com.sliit.studentplatform.module3.dto.response.TaskResponse;
import com.sliit.studentplatform.module3.dto.response.TaskSummaryResponse;
import com.sliit.studentplatform.module3.enums.TaskPriority;
import com.sliit.studentplatform.module3.enums.TaskStatus;
import com.sliit.studentplatform.module3.service.interfaces.ITaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RestController
@RequestMapping({"/api/v1", "/api/module3"})
@RequiredArgsConstructor
@Tag(name = "Tasks")
public class TaskController {

    private final ITaskService taskService;
    private final com.sliit.studentplatform.auth.repository.UserRepository userRepository;

    private Long getUserId(UserPrincipal principal) {
        if (principal != null) return principal.getId();
        return userRepository.findAll().stream()
            .findFirst()
            .map(com.sliit.studentplatform.auth.entity.User::getId)
            .orElse(1L);
    }

    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskResponse>> create(
            @Valid @RequestBody TaskRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(taskService.createTask(req, userId), "Task created"));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskById(id), "Task details retrieved"));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> update(@PathVariable Long id,
                                                           @Valid @RequestBody TaskRequest req,
                                                           @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTask(id, req, userId), "Task updated"));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = getUserId(principal);
        taskService.deleteTask(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Task deleted"));
    }

    @PutMapping("/tasks/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(@PathVariable Long id,
                                                                 @RequestBody Map<String, Object> body,
                                                                 @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = getUserId(principal);
        TaskStatus status = TaskStatus.valueOf(body.get("status").toString());
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTaskStatus(id, status, userId), "Task status updated"));
    }

    @PatchMapping("/tasks/{id}/assign")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(@PathVariable Long id,
                                                               @RequestBody Map<String, Long> body,
                                                               @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = getUserId(principal);
        Long assigneeId = body.get("assigneeId");
        return ResponseEntity.ok(ApiResponse.success(taskService.assignTask(id, assigneeId, userId), "Task assigned"));
    }

    @PatchMapping("/tasks/{id}/complete")
    public ResponseEntity<ApiResponse<TaskResponse>> completeTask(@PathVariable Long id,
                                                                 @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTaskStatus(id, TaskStatus.DONE, userId), "Task marked as completed"));
    }

    @GetMapping("/groups/{groupId}/tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getGroupTasks(@PathVariable Long groupId,
                                                                        @RequestParam(required = false) TaskStatus status,
                                                                        @RequestParam(required = false) TaskPriority priority,
                                                                        @RequestParam(required = false) Long assignedToId,
                                                                        @RequestParam(required = false) LocalDate dueBefore,
                                                                        @RequestParam(required = false) LocalDate dueAfter) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getGroupTasks(groupId, status, priority, assignedToId, dueBefore, dueAfter), "Group tasks retrieved"));
    }

    @GetMapping("/groups/{groupId}/tasks/summary")
    public ResponseEntity<ApiResponse<TaskSummaryResponse>> getGroupSummary(@PathVariable Long groupId) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getGroupTaskSummary(groupId), "Group task summary retrieved"));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getMyTasks(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(ApiResponse.success(taskService.getTasksByUser(userId), "Your tasks retrieved"));
    }

    @GetMapping("/tasks/kanban/{projectId}")
    public ResponseEntity<ApiResponse<KanbanBoardResponse>> getBoard(@PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getKanbanBoard(projectId), "Kanban board retrieved"));
    }
}
