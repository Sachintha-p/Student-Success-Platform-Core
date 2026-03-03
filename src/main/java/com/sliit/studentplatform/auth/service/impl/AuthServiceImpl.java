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
import com.sliit.studentplatform.common.service.EmailService;
import com.sliit.studentplatform.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;

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
  private final EmailService emailService;

  // ─────────────────────── OTP Login (Fixed for Debugging) ───────────────────

  @Override
  @Transactional // This only covers the DB registration part now
  public void requestOtp(String email) {
    log.info("Processing OTP request for: {}", email);

    if (!email.endsWith("@sliit.lk") && !email.endsWith("@my.sliit.lk")) {
      throw new UnauthorizedException("Only SLIIT email addresses are allowed");
    }

    User user = userRepository.findByEmail(email).orElseGet(() -> {
      log.info("Auto-registering new SLIIT user: {}", email);
      return userRepository.save(User.builder()
              .email(email)
              .fullName(email.split("@")[0])
              .password(passwordEncoder.encode("OTP_SESSION_STUB"))
              .role(email.endsWith("@my.sliit.lk") ? Role.STUDENT : Role.ADMIN)
              .enabled(true)
              .build());
    });

    String otp = String.format("%06d", new Random().nextInt(999999));
    user.setOtp(otp);
    user.setOtpExpiry(LocalDateTime.now().plusMinutes(15)); // Increased to 15 mins for easier testing
    userRepository.save(user);

    // Commit the DB change immediately so you can see it in Neon
    userRepository.flush();

    // Send email in a try-catch block so it doesn't crash the whole request
    try {
      emailService.sendOtpEmail(email, otp);
      log.info("OTP email successfully sent to: {}", email);
    } catch (Exception e) {
      log.error("EMAIL DELIVERY FAILED but OTP is saved in DB. Error: {}", e.getMessage());
      log.info(">>> DEBUG OTP FOR MANUAL ENTRY: {} <<<", otp);
      // We don't re-throw the error so Postman gets a 200 OK
    }
  }

  @Override
  @Transactional
  public AuthResponse verifyOtp(String email, String otp) {
    log.info("Verifying OTP for: {}", email);
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

    if (user.getOtp() == null || !user.getOtp().equals(otp)) {
      throw new UnauthorizedException("Invalid OTP provided");
    }

    if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
      throw new UnauthorizedException("OTP has expired. Please request a new one.");
    }

    user.setOtp(null);
    user.setOtpExpiry(null);
    userRepository.save(user);

    Authentication authentication = new UsernamePasswordAuthenticationToken(
            UserPrincipal.create(user),
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
    );

    return buildAuthResponse(user,
            jwtTokenProvider.generateAccessToken(authentication),
            jwtTokenProvider.generateRefreshToken(user.getId()));
  }

  // ─────────────────────── Standard Auth Methods ──────────────────────────

  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ConflictException("User already exists with email: " + request.getEmail());
    }

    User user = User.builder()
            .fullName(request.getFullName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .enabled(true)
            .build();
    user = userRepository.save(user);

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

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    return buildAuthResponse(user, jwtTokenProvider.generateAccessToken(authentication),
            jwtTokenProvider.generateRefreshToken(user.getId()));
  }

  @Override
  public AuthResponse login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

    return buildAuthResponse(user, jwtTokenProvider.generateAccessToken(authentication),
            jwtTokenProvider.generateRefreshToken(user.getId()));
  }

  @Override
  public AuthResponse refreshToken(String refreshToken) {
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new UnauthorizedException("Invalid refresh token.");
    }
    Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService
            .loadUserByUsername(String.valueOf(userId));
    Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

    return buildAuthResponse(user, jwtTokenProvider.generateAccessToken(authentication),
            jwtTokenProvider.generateRefreshToken(userId));
  }

  @Override
  public UserProfileResponse getProfile(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
            .id(user.getId()).fullName(user.getFullName()).email(user.getEmail())
            .role(user.getRole()).enabled(user.isEnabled());

    if (user.getRole() == Role.STUDENT) {
      studentRepository.findByUserId(userId).ifPresent(student -> {
        builder.registrationNumber(student.getRegistrationNumber())
                .degreeProgramme(student.getDegreeProgramme())
                .yearOfStudy(student.getYearOfStudy())
                .semester(student.getSemester());
      });
    }
    return builder.build();
  }

  private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
    return AuthResponse.builder()
            .accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer")
            .expiresIn(jwtConfig.getExpiration()).userId(user.getId())
            .email(user.getEmail()).fullName(user.getFullName()).role(user.getRole())
            .build();
  }
}