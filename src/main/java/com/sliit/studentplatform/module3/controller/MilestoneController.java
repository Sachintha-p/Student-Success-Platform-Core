package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.dto.request.CreateMilestoneRequest;
import com.sliit.studentplatform.module3.dto.response.MilestoneResponse;
import com.sliit.studentplatform.module3.service.interfaces.IMilestoneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
@Tag(name = "Project Milestones")
public class MilestoneController {
  private final IMilestoneService milestoneService;

  @PostMapping
  public ResponseEntity<ApiResponse<MilestoneResponse>> create(@Valid @RequestBody CreateMilestoneRequest req,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(milestoneService.createMilestone(req, user.getId()), "Milestone created"));
  }

  @GetMapping("/group/{groupId}")
  public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getByGroup(@PathVariable Long groupId) {
    return ResponseEntity
        .ok(ApiResponse.success(milestoneService.getMilestonesForGroup(groupId), "Milestones retrieved"));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponse<MilestoneResponse>> updateStatus(@PathVariable Long id,
      @RequestParam String status, @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity
        .ok(ApiResponse.success(milestoneService.updateMilestoneStatus(id, status, user.getId()), "Status updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
    milestoneService.deleteMilestone(id, user.getId());
    return ResponseEntity.ok(ApiResponse.success("Milestone deleted"));
  }
}
