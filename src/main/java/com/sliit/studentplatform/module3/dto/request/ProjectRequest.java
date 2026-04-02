package com.sliit.studentplatform.module3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

  @NotBlank(message = "Project name is required")
  @Size(max = 150, message = "Project name must not exceed 150 characters")
  private String name;

  private String description;

  @NotNull(message = "Start date is required")
  private LocalDate startDate;

  @NotNull(message = "End date is required")
  private LocalDate endDate;

  @NotNull(message = "Team ID is required")
  private Long teamId;
}
