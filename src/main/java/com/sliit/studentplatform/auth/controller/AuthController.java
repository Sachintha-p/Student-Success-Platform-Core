package com.sliit.studentplatform.auth.controller;

import com.sliit.studentplatform.auth.dto.request.EmailLoginRequest;
import com.sliit.studentplatform.auth.dto.request.LoginRequest;
import com.sliit.studentplatform.auth.dto.request.VerifyOtpRequest;
import com.sliit.studentplatform.auth.dto.response.AuthResponse;
import com.sliit.studentplatform.auth.dto.response.UserProfileResponse;
import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.auth.service.interfaces.IAuthService;
import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final IAuthService authService;
  private final UserRepository userRepository;

  @PostMapping("/login/email")
  public ResponseEntity<String> requestEmailOtp(@RequestBody EmailLoginRequest request) {
    authService.requestOtp(request.getEmail());
    return ResponseEntity.ok("OTP sent to your SLIIT email.");
  }

  @PostMapping("/login/verify-otp")
  public ResponseEntity<AuthResponse> verifyEmailOtp(@RequestBody VerifyOtpRequest request) {
    AuthResponse response = authService.verifyOtp(request.getEmail(), request.getOtp());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> loginWithPassword(@RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/profile")
  public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
          @AuthenticationPrincipal UserPrincipal currentUser) {

    User user = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

    UserProfileResponse response = UserProfileResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .role(user.getRole())
            .enabled(user.isEnabled())
            .build();

    return ResponseEntity.ok(ApiResponse.success(response, "Profile retrieved successfully"));
  }
}