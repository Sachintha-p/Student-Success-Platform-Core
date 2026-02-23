package com.sliit.studentplatform.module3.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
  private Long id;
  private Long milestoneId;
  private Long assigneeId;
  private String assigneeName;
  private String title;
  private String description;
  private LocalDate dueDate;
  private String status;
  private String priority;
}
