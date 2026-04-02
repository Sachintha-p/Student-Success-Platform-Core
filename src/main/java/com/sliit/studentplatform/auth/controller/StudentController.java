package com.sliit.studentplatform.auth.controller;

import com.sliit.studentplatform.auth.dto.request.UpdateStudentProfileRequest;
import com.sliit.studentplatform.auth.dto.response.UserProfileResponse;
import com.sliit.studentplatform.auth.service.interfaces.IStudentService;
import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final IStudentService studentService;

    // 1. Get the current logged-in student's profile
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        UserProfileResponse profile = studentService.getCurrentProfile(currentUser.getId());

        // ADDED THE REQUIRED MESSAGE STRING HERE
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile retrieved successfully"));
    }

    // 2. Update the current logged-in student's profile
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody UpdateStudentProfileRequest request) {

        UserProfileResponse updatedProfile = studentService.updateProfile(currentUser.getId(), request);

        // ADDED THE REQUIRED MESSAGE STRING HERE
        return ResponseEntity.ok(ApiResponse.success(updatedProfile, "Profile updated successfully"));
    }
}