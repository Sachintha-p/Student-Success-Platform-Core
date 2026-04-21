package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.dto.request.MilestoneRequest;
import com.sliit.studentplatform.module3.dto.response.AllProjectsMilestonesResponse;
import com.sliit.studentplatform.module3.dto.response.MilestoneProgressSummaryResponse;
import com.sliit.studentplatform.module3.dto.response.MilestoneResponse;
import com.sliit.studentplatform.module3.dto.response.MilestoneTimelineResponse;
import com.sliit.studentplatform.module3.service.interfaces.IMilestoneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RestController
@RequestMapping({"/api/v1/milestones", "/api/module3/milestones"})
@RequiredArgsConstructor
@Tag(name = "Milestones")
public class MilestoneController {

  private final IMilestoneService milestoneService;
  private final com.sliit.studentplatform.auth.repository.UserRepository userRepository;

  private Long getUserId(UserPrincipal principal) {
    if (principal != null) return principal.getId();
    return userRepository.findAll().stream()
        .findFirst()
        .map(com.sliit.studentplatform.auth.entity.User::getId)
        .orElse(1L);
  }

  @PostMapping
  public ResponseEntity<ApiResponse<MilestoneResponse>> create(
      @Valid @RequestBody MilestoneRequest req,
      @AuthenticationPrincipal UserPrincipal principal) {
    Long userId = getUserId(principal);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(milestoneService.createMilestone(req, userId), "Milestone created"));
  }

  @GetMapping("/project/{projectId}")
  public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getByProject(@PathVariable Long projectId) {
    return ResponseEntity.ok(ApiResponse.success(milestoneService.getMilestonesByProject(projectId), "Milestones retrieved for project"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<MilestoneResponse>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(milestoneService.getMilestoneById(id), "Milestone details retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<MilestoneResponse>> update(@PathVariable Long id, 
      @Valid @RequestBody MilestoneRequest req,
      @AuthenticationPrincipal UserPrincipal principal) {
    Long userId = getUserId(principal);
    return ResponseEntity.ok(ApiResponse.success(milestoneService.updateMilestone(id, req, userId), "Milestone updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, 
      @AuthenticationPrincipal UserPrincipal principal) {
    Long userId = getUserId(principal);
    milestoneService.deleteMilestone(id, userId);
    return ResponseEntity.ok(ApiResponse.success(null, "Milestone deleted"));
  }

  @PatchMapping("/{id}/progress")
  public ResponseEntity<ApiResponse<MilestoneResponse>> updateProgress(@PathVariable Long id, 
      @RequestBody Map<String, Integer> body, 
      @AuthenticationPrincipal UserPrincipal principal) {
    Long userId = getUserId(principal);
    int progress = body.getOrDefault("progressPercentage", 0);
    return ResponseEntity.ok(ApiResponse.success(milestoneService.updateProgress(id, progress, userId), "Milestone progress updated"));
  }

  @GetMapping("/project/{projectId}/timeline")
  public ResponseEntity<ApiResponse<MilestoneTimelineResponse>> getTimeline(@PathVariable Long projectId) {
    return ResponseEntity.ok(ApiResponse.success(milestoneService.getTimeline(projectId), "Project timeline retrieved"));
  }

  @GetMapping("/project/{projectId}/summary")
  public ResponseEntity<ApiResponse<MilestoneProgressSummaryResponse>> getSummary(@PathVariable Long projectId) {
    return ResponseEntity.ok(ApiResponse.success(milestoneService.getProgressSummary(projectId), "Project progress summary retrieved"));
  }

  @GetMapping("/project/{projectId}/upcoming")
  public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getUpcoming(@PathVariable Long projectId, 
      @RequestParam(defaultValue = "7") int days) {
    return ResponseEntity.ok(ApiResponse.success(milestoneService.getUpcomingDeadlines(projectId, days), "Upcoming deadlines retrieved"));
  }

  @GetMapping("/all-projects/timeline")
  public ResponseEntity<ApiResponse<AllProjectsMilestonesResponse>> getAllProjectsTimeline() {
    return ResponseEntity.ok(ApiResponse.success(milestoneService.getAllProjectsTimeline(), "All projects timeline retrieved"));
  }
}
