package com.sliit.studentplatform.module4.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQueryResponse {
  private Long conversationId;
  private Long messageId;
  private String answer;
  private String model;
  private Integer tokenCount;
  private java.time.LocalDateTime timestamp;
}
