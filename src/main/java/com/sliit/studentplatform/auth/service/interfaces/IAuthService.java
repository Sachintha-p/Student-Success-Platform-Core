package com.sliit.studentplatform.auth.service.interfaces;

import com.sliit.studentplatform.auth.dto.request.LoginRequest;
import com.sliit.studentplatform.auth.dto.request.RegisterRequest;
import com.sliit.studentplatform.auth.dto.response.AuthResponse;
import com.sliit.studentplatform.auth.dto.response.UserProfileResponse;

/**
 * Authentication service contract.
 *
 * <p>
 * Segregated from profile operations per Interface Segregation Principle.
 */
public interface IAuthService {

  /**
   * Registers a new user (and optionally a student profile).
   *
   * @param request registration payload
   * @return JWT tokens and basic user info
   */
  AuthResponse register(RegisterRequest request);

  /**
   * Authenticates a user with email/password.
   *
   * @param request login payload
   * @return JWT access + refresh tokens
   */
  AuthResponse login(LoginRequest request);

  /**
   * Exchanges a valid refresh token for a new access token.
   *
   * @param refreshToken the refresh JWT string
   * @return new JWT access token response
   */
  AuthResponse refreshToken(String refreshToken);

  /**
   * Returns the full profile of the currently authenticated user.
   *
   * @param userId the authenticated user's ID
   * @return user profile DTO
   */
  UserProfileResponse getProfile(Long userId);
}
