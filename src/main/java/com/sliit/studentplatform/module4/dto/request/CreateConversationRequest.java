package com.sliit.studentplatform.module4.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
  @NotBlank
  @Size(max = 200)
  private String title;
  @Size(max = 100)
  private String subject;
}
