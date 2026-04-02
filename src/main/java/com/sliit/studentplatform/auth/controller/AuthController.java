package com.sliit.studentplatform.auth.controller;

import com.sliit.studentplatform.auth.dto.request.EmailLoginRequest;
import com.sliit.studentplatform.auth.dto.request.LoginRequest;
import com.sliit.studentplatform.auth.dto.request.VerifyOtpRequest;
import com.sliit.studentplatform.auth.dto.response.AuthResponse;
<<<<<<< feature/Event-managmenr01
import com.sliit.studentplatform.auth.service.interfaces.IAuthService; // Use Interface, not Impl
import jakarta.validation.Valid;
=======
import com.sliit.studentplatform.auth.service.interfaces.IAuthService;
>>>>>>> dev
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final IAuthService authService;

  @PostMapping("/login/email")
  public ResponseEntity<String> requestEmailOtp(@Valid @RequestBody EmailLoginRequest request) {
    authService.requestOtp(request.getEmail());
    return ResponseEntity.ok("OTP sent to your SLIIT email.");
  }

<<<<<<< feature/Event-managmenr01
  @PostMapping("/login/verify")
  public ResponseEntity<AuthResponse> verifyEmailOtp(@Valid @RequestBody VerifyOtpRequest request) {
=======
  @PostMapping("/login/verify-otp")
  public ResponseEntity<AuthResponse> verifyEmailOtp(@RequestBody VerifyOtpRequest request) {
>>>>>>> dev
    AuthResponse response = authService.verifyOtp(request.getEmail(), request.getOtp());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> loginWithPassword(@RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  /**
   * DYNAMIC PROFILE ENDPOINT:
   * This now grabs the real email from the logged-in user's token!
   */
  @GetMapping("/profile")
  public ResponseEntity<?> getUserProfile(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.status(401).body("Not authenticated");
    }

    // Grabs the email/username stored in the JWT Subject
    String userEmail = authentication.getName();

    // Determine if it's the admin or a student to set a display name
    String displayName = userEmail.contains("admin") ? "System Admin" : "Student Member";

    return ResponseEntity.ok(Map.of(
            "fullName", displayName,
            "email", userEmail,
            "role", authentication.getAuthorities().toString()
    ));
  }
}