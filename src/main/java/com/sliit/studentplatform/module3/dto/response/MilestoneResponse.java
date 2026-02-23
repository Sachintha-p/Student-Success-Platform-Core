package com.sliit.studentplatform.module3.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneResponse {
  private Long id;
  private Long groupId;
  private String title;
  private String description;
  private LocalDate dueDate;
  private String status;
}
