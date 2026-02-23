package com.sliit.studentplatform.module2.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module2.dto.request.JobApplicationRequest;
import com.sliit.studentplatform.module2.entity.JobApplication;
import com.sliit.studentplatform.module2.service.impl.JobApplicationServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job-applications")
@RequiredArgsConstructor
@Tag(name = "Job Applications", description = "Apply to job listings")
public class ApplicationController {

  private final JobApplicationServiceImpl applicationService;

  @PostMapping
  public ResponseEntity<ApiResponse<JobApplication>> apply(
      @Valid @RequestBody JobApplicationRequest request,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(applicationService.apply(request, currentUser.getId()), "Application submitted"));
  }
}
