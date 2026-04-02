package com.sliit.studentplatform.module2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {

  @NotNull
  private Long jobListingId;

  private Long resumeId;

  @NotBlank(message = "Name is required")
  private String fullName;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Phone number is required")
  private String phoneNumber;

  @Size(max = 3000)
  private String coverLetter;
}