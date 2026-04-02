package com.sliit.studentplatform.module3.dto.response;

import com.sliit.studentplatform.module3.enums.MilestoneStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneResponse {
  private Long id;
  private Long projectId;
  private String title;
  private String description;
  private LocalDate startDate;
  private LocalDate dueDate;
  private MilestoneStatus status;
  private int progressPercentage;
  private Long assignedToId;
  private boolean isOverdue;
  private boolean isUpcoming;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
