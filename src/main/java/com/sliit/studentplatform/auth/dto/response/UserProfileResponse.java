package com.sliit.studentplatform.auth.dto.response;

import com.sliit.studentplatform.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response DTO for the authenticated user's profile. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

  private Long id;
  private String fullName;
  private String email;
  private Role role;
  private boolean enabled;

  // Student-specific (null for non-student users)
  private String registrationNumber;
  private String degreeProgramme;
  private Integer yearOfStudy;
  private Integer semester;
  private Double gpa;
  private String[] skills;
  private String bio;
  private String profilePictureUrl;
}
