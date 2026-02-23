package com.sliit.studentplatform.module2.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module2.dto.request.JobListingRequest;
import com.sliit.studentplatform.module2.entity.JobListing;
import com.sliit.studentplatform.module2.service.interfaces.IJobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Listings", description = "Post and browse job/internship listings")
public class JobController {

  private final IJobService jobService;

  @PostMapping
  public ResponseEntity<ApiResponse<JobListing>> create(
      @Valid @RequestBody JobListingRequest request,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(jobService.createListing(request, currentUser.getId()), "Listing created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<JobListing>>> listActive(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity
        .ok(ApiResponse.success(jobService.listActiveJobs(PageRequest.of(page, size)), "Jobs retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<JobListing>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(jobService.getListingById(id), "Job retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<JobListing>> update(
      @PathVariable Long id, @Valid @RequestBody JobListingRequest request,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity
        .ok(ApiResponse.success(jobService.updateListing(id, request, currentUser.getId()), "Listing updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    jobService.deleteListing(id, currentUser.getId());
    return ResponseEntity.ok(ApiResponse.success("Listing deactivated"));
  }
}
