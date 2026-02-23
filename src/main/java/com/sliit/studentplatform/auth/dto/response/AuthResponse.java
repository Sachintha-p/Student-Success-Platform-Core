package com.sliit.studentplatform.auth.dto.response;

import com.sliit.studentplatform.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response DTO returned after a successful login or token refresh. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

  private String accessToken;
  private String refreshToken;

  @Builder.Default
  private String tokenType = "Bearer";

  private Long expiresIn;

  private Long userId;
  private String email;
  private String fullName;
  private Role role;
}
