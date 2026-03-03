package com.sliit.studentplatform.auth.service.interfaces;

import com.sliit.studentplatform.auth.dto.request.LoginRequest;
import com.sliit.studentplatform.auth.dto.request.RegisterRequest;
import com.sliit.studentplatform.auth.dto.response.AuthResponse;
import com.sliit.studentplatform.auth.dto.response.UserProfileResponse;

/**
 * Authentication service contract.
 */
public interface IAuthService {

  /**
   * Registers a new user (and optionally a student profile).
   */
  AuthResponse register(RegisterRequest request);

  /**
   * Authenticates a user with email/password.
   */
  AuthResponse login(LoginRequest request);

  /**
   * Generates and sends a 6-digit OTP to a valid SLIIT email address.
   * Restricted to @sliit.lk or @my.sliit.lk domains.
   *
   * @param email the user's SLIIT email address
   */
  void requestOtp(String email);

  /**
   * Verifies the provided OTP and returns JWT tokens upon success.
   *
   * @param email the user's email address
   * @param otp the 6-digit verification code
   * @return JWT access + refresh tokens
   */
  AuthResponse verifyOtp(String email, String otp);

  /**
   * Exchanges a valid refresh token for a new access token.
   */
  AuthResponse refreshToken(String refreshToken);

  /**
   * Returns the full profile of the currently authenticated user.
   */
  UserProfileResponse getProfile(Long userId);
}