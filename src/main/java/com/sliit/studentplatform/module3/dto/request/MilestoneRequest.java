package com.sliit.studentplatform.module3.dto.request;

import com.sliit.studentplatform.module3.enums.MilestoneStatus;
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
public class MilestoneRequest {

  @NotNull(message = "Project ID is required")
  private Long projectId;

  @NotBlank(message = "Milestone title is required")
  @Size(max = 150, message = "Milestone title must not exceed 150 characters")
  private String title;

  private String description;

  @NotNull(message = "Start date is required")
  private LocalDate startDate;

  @NotNull(message = "Due date is required")
  private LocalDate dueDate;

  private MilestoneStatus status;

  private Integer progressPercentage;

  private Long assignedToId;
}
