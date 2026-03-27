package com.sliit.studentplatform.auth.controller;

import com.sliit.studentplatform.auth.dto.request.EmailLoginRequest; // Fixed path
import com.sliit.studentplatform.auth.dto.request.VerifyOtpRequest; // Fixed path
import com.sliit.studentplatform.auth.dto.response.AuthResponse;
import com.sliit.studentplatform.auth.service.interfaces.IAuthService; // Use Interface, not Impl
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final IAuthService authService; // Better practice to use the interface

  @PostMapping("/login/email")
  public ResponseEntity<String> requestEmailOtp(@Valid @RequestBody EmailLoginRequest request) {
    authService.requestOtp(request.getEmail());
    return ResponseEntity.ok("OTP sent to your SLIIT email.");
  }

  @PostMapping("/login/verify")
  public ResponseEntity<AuthResponse> verifyEmailOtp(@Valid @RequestBody VerifyOtpRequest request) {
    AuthResponse response = authService.verifyOtp(request.getEmail(), request.getOtp());
    return ResponseEntity.ok(response);
  }
}