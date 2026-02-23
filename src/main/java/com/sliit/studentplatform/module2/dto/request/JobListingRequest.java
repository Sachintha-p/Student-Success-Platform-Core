package com.sliit.studentplatform.module2.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobListingRequest {

  @NotBlank
  @Size(max = 200)
  private String title;

  @NotBlank
  @Size(max = 200)
  private String company;

  @Size(max = 5000)
  private String description;

  private String[] requiredSkills;

  @NotBlank
  private String type;

  private String location;
  private boolean remote;

  @Future(message = "Deadline must be in the future")
  private LocalDate deadline;
}
