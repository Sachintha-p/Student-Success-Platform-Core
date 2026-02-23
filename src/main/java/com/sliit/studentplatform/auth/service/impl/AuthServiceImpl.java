package com.sliit.studentplatform.auth.service.impl;

import com.sliit.studentplatform.auth.dto.request.LoginRequest;
import com.sliit.studentplatform.auth.dto.request.RegisterRequest;
import com.sliit.studentplatform.auth.dto.response.AuthResponse;
import com.sliit.studentplatform.auth.dto.response.UserProfileResponse;
import com.sliit.studentplatform.auth.entity.Student;
import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.StudentRepository;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.auth.service.interfaces.IAuthService;
import com.sliit.studentplatform.common.enums.Role;
import com.sliit.studentplatform.common.exception.ConflictException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.common.security.JwtTokenProvider;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link IAuthService}.
 *
 * <p>
 * Handles user registration (with optional student profile creation),
 * login, refresh token exchange, and profile retrieval.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

  private final UserRepository userRepository;
  private final StudentRepository studentRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtConfig jwtConfig;
  private final UserDetailsService userDetailsService;

  // ─────────────────────── Register ────────────────────────────────────────

  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    log.info("Registering new user with email: {}", request.getEmail());

    // 1. Check uniqueness
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ConflictException("A user with email '" + request.getEmail() + "' already exists");
    }

    if (request.getRole() == Role.STUDENT
        && request.getRegistrationNumber() != null
        && studentRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
      throw new ConflictException("Registration number '" + request.getRegistrationNumber()
          + "' is already in use");
    }

    // 2. Create and persist User
    User user = User.builder()
        .fullName(request.getFullName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .enabled(true)
        .build();
    user = userRepository.save(user);

    // 3. Create Student profile if role is STUDENT
    if (request.getRole() == Role.STUDENT) {
      Student student = Student.builder()
          .user(user)
          .registrationNumber(request.getRegistrationNumber())
          .degreeProgramme(request.getDegreeProgramme())
          .yearOfStudy(request.getYearOfStudy())
          .semester(request.getSemester())
          .build();
      studentRepository.save(student);
    }

    // 4. Authenticate and generate tokens
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    String accessToken = jwtTokenProvider.generateAccessToken(authentication);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

    log.info("User registered successfully with id: {}", user.getId());
    return buildAuthResponse(user, accessToken, refreshToken);
  }

  // ─────────────────────── Login ────────────────────────────────────────────

  @Override
  public AuthResponse login(LoginRequest request) {
    log.info("Login attempt for email: {}", request.getEmail());

    // TODO: add brute-force protection (rate limiting per IP)
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    User user = userRepository.findById(principal.getId())
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

    String accessToken = jwtTokenProvider.generateAccessToken(authentication);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

    log.info("User {} logged in successfully", user.getId());
    return buildAuthResponse(user, accessToken, refreshToken);
  }

  // ─────────────────────── Refresh Token ───────────────────────────────────

  @Override
  public AuthResponse refreshToken(String refreshToken) {
    log.info("Refresh token request");

    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new UnauthorizedException("Invalid or expired refresh token");
    }

    Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    // Build a new authentication to generate access token
    org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService
        .loadUserByUsername(String.valueOf(userId));
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());

    String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

    return buildAuthResponse(user, newAccessToken, newRefreshToken);
  }

  // ─────────────────────── Profile ─────────────────────────────────────────

  @Override
  public UserProfileResponse getProfile(Long userId) {
    log.info("Fetching profile for user id: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .role(user.getRole())
        .enabled(user.isEnabled());

    if (user.getRole() == Role.STUDENT) {
      studentRepository.findByUserId(userId).ifPresent(student -> {
        builder
            .registrationNumber(student.getRegistrationNumber())
            .degreeProgramme(student.getDegreeProgramme())
            .yearOfStudy(student.getYearOfStudy())
            .semester(student.getSemester())
            .gpa(student.getGpa())
            .skills(student.getSkills())
            .bio(student.getBio())
            .profilePictureUrl(student.getProfilePictureUrl());
      });
    }

    return builder.build();
  }

  // ─────────────────────── Helpers ─────────────────────────────────────────

  private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(jwtConfig.getExpiration())
        .userId(user.getId())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .role(user.getRole())
        .build();
  }
}
