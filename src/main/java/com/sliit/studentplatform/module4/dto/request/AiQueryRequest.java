package com.sliit.studentplatform.module4.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQueryRequest {
  @NotNull
  private Long conversationId;
  @NotBlank
  @Size(max = 5000)
  private String query;
  private String subject;
}
