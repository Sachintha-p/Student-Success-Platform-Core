package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.dto.request.ProjectRequest;
import com.sliit.studentplatform.module3.dto.response.ProjectResponse;
import com.sliit.studentplatform.module3.service.interfaces.IProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RestController
@RequestMapping({"/api/v1/projects", "/api/module3/projects"})
@RequiredArgsConstructor
@Tag(name = "Projects")
public class ProjectController {

  private final IProjectService projectService;
  private final com.sliit.studentplatform.auth.repository.UserRepository userRepository;

  private Long getUserId(UserPrincipal principal) {
    if (principal != null) return principal.getId();
    return userRepository.findAll().stream()
        .findFirst()
        .map(com.sliit.studentplatform.auth.entity.User::getId)
        .orElse(1L);
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ProjectResponse>> create(
      @Valid @RequestBody ProjectRequest req,
      @AuthenticationPrincipal UserPrincipal principal) {
    Long userId = getUserId(principal);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(projectService.createProject(req, userId), "Project created"));
  }

  @GetMapping("/all")
  public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects() {
    return ResponseEntity.ok(ApiResponse.success(projectService.getAllProjects(), "All projects retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProjectResponse>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(projectService.getProjectById(id), "Project details retrieved"));
  }

  @GetMapping("/team/{teamId}")
  public ResponseEntity<ApiResponse<List<ProjectResponse>>> getByTeam(@PathVariable Long teamId) {
    return ResponseEntity.ok(ApiResponse.success(projectService.getProjectsByTeam(teamId), "Projects retrieved for team"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ProjectResponse>> update(@PathVariable Long id, 
      @Valid @RequestBody ProjectRequest req,
      @AuthenticationPrincipal UserPrincipal principal) {
    Long userId = getUserId(principal);
    return ResponseEntity.ok(ApiResponse.success(projectService.updateProject(id, req, userId), "Project updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, 
      @AuthenticationPrincipal UserPrincipal principal) {
    Long userId = getUserId(principal);
    projectService.deleteProject(id, userId);
    return ResponseEntity.ok(ApiResponse.success(null, "Project deleted"));
  }

  @GetMapping("/{id}/progress")
  public ResponseEntity<ApiResponse<Double>> getProgress(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(projectService.calculateProjectProgress(id), "Project progress calculated"));
  }
}
