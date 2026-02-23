package com.sliit.studentplatform.auth.controller;

import com.sliit.studentplatform.auth.dto.request.LoginRequest;
import com.sliit.studentplatform.auth.dto.request.RegisterRequest;
import com.sliit.studentplatform.auth.dto.response.AuthResponse;
import com.sliit.studentplatform.auth.dto.response.UserProfileResponse;
import com.sliit.studentplatform.auth.service.interfaces.IAuthService;
import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 *
 * <p>
 * All endpoints are public (no JWT required) except {@code /me}.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, token refresh, and profile endpoints")
public class AuthController {

  private final IAuthService authService;

  @Operation(summary = "Register a new user")
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthResponse>> register(
      @Valid @RequestBody RegisterRequest request) {
    AuthResponse response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(response, "User registered successfully"));
  }

  @Operation(summary = "Authenticate and receive JWT tokens")
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
  }

  @Operation(summary = "Refresh access token using a valid refresh token")
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResponse>> refresh(
      @RequestParam String refreshToken) {
    AuthResponse response = authService.refreshToken(refreshToken);
    return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
  }

  @Operation(summary = "Get currently authenticated user's profile")
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    UserProfileResponse profile = authService.getProfile(currentUser.getId());
    return ResponseEntity.ok(ApiResponse.success(profile, "Profile retrieved"));
  }
}
