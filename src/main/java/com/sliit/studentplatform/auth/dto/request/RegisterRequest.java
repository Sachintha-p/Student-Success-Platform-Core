package com.sliit.studentplatform.auth.dto.request;

import com.sliit.studentplatform.common.enums.Role;
import com.sliit.studentplatform.common.validation.RegistrationNumberValidator;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for new user registration.
 *
 * <p>
 * Student-specific fields (registrationNumber, degreeProgramme, yearOfStudy)
 * are optional for non-student roles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

  @NotBlank(message = "Full name is required")
  @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
  private String fullName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be a valid address")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
  private String password;

  @NotNull(message = "Role is required")
  private Role role;

  // ── Student-specific (required when role = STUDENT) ──────────────────────

  @RegistrationNumberValidator
  private String registrationNumber;

  @Size(max = 100)
  private String degreeProgramme;

  @Min(value = 1, message = "Year of study must be at least 1")
  @Max(value = 4, message = "Year of study cannot exceed 4")
  private Integer yearOfStudy;

  @Min(value = 1)
  @Max(value = 8)
  private Integer semester;
}
