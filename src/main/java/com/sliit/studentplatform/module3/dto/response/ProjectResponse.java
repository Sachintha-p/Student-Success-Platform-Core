package com.sliit.studentplatform.module3.dto.response;

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
public class ProjectResponse {
  private Long id;
  private String name;
  private String description;
  private LocalDate startDate;
  private LocalDate endDate;
  private Long createdById;
  private Long teamId;
  private double progressPercentage;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
