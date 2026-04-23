package com.sliit.studentplatform.module2.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module2.dto.request.JobListingRequest;
import com.sliit.studentplatform.module2.service.interfaces.IJobListingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/jobs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin Job Management", description = "Admin endpoints for managing job listings")
public class AdminJobController {

    private final IJobListingService jobListingService;

    // 1. CREATE A JOB
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createJob(
            @Valid @RequestBody JobListingRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        var createdJob = jobListingService.createJob(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdJob, "Job listing created successfully"));
    }

    // 2. EDIT / UPDATE A JOB
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobListingRequest request) {
        var updatedJob = jobListingService.updateJob(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedJob, "Job listing updated successfully"));
    }

    // 3. DELETE A JOB
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        jobListingService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.success("Job listing deleted successfully"));
    }

    // 4. GET ALL JOBS (>>> NEW ADDITION <<<)
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllJobs() {
        // NOTE: Make sure your IJobListingService actually has a method named getAllJobs()!
        // If it's named something like findAll() or getJobs(), just change the name below.
        var jobs = jobListingService.getAllJobs();
        return ResponseEntity.ok(ApiResponse.success(jobs, "Jobs retrieved successfully"));
    }
}