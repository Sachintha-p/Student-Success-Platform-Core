package com.sliit.studentplatform.module2.controller;

import com.sliit.studentplatform.common.enums.Status;
import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module2.dto.request.JobApplicationRequest;
import com.sliit.studentplatform.module2.dto.response.JobApplicationResponse;
import com.sliit.studentplatform.module2.service.interfaces.IJobApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-applications")
@RequiredArgsConstructor
@Tag(name = "Job Applications", description = "Student job application workflow")
public class ApplicationController {

  private final IJobApplicationService applicationService;

  // --- STUDENT ENDPOINTS ---

  @PostMapping
  public ResponseEntity<ApiResponse<JobApplicationResponse>> apply(
          @Valid @RequestBody JobApplicationRequest request,
          @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(applicationService.apply(request, currentUser.getId()), "Application submitted successfully"));
  }

  @GetMapping("/my")
  public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getMyApplications(
          @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(applicationService.getMyApplications(currentUser.getId()), "Your applications retrieved"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> withdraw(
          @PathVariable Long id,
          @AuthenticationPrincipal UserPrincipal currentUser) {
    applicationService.withdrawApplication(id, currentUser.getId());
    return ResponseEntity.ok(ApiResponse.success("Application withdrawn"));
  }

  // --- ADMIN / RECRUITER ENDPOINTS ---

  @GetMapping("/job/{jobId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getJobApplicants(@PathVariable Long jobId) {
    return ResponseEntity.ok(ApiResponse.success(applicationService.getApplicationsForJob(jobId), "Job applicants retrieved"));
  }

  // NEW: Get all applications globally
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getAllApplications() {
    return ResponseEntity.ok(ApiResponse.success(applicationService.getAllApplications(), "All applications retrieved"));
  }

  // NEW: Admin force delete an application
  @DeleteMapping("/admin/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> deleteApplicationAdmin(@PathVariable Long id) {
    applicationService.deleteApplicationAdmin(id);
    return ResponseEntity.ok(ApiResponse.success("Application deleted permanently"));
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<JobApplicationResponse>> updateStatus(
          @PathVariable Long id,
          @RequestParam Status status,
          @RequestParam(required = false) String notes) {
    return ResponseEntity.ok(ApiResponse.success(applicationService.updateApplicationStatus(id, status, notes), "Application status updated"));
  }
}