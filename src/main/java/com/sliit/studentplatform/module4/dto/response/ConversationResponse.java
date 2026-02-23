package com.sliit.studentplatform.module4.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
  private Long id;
  private Long userId;
  private String title;
  private String subject;
  private boolean active;
  private LocalDateTime createdAt;
}
