package com.sliit.studentplatform.module2.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CvSuggestionResponse {
  private Long id;
  private Long resumeId;
  private String section;
  private String suggestion;
  private String priority;
  private boolean applied;
}
