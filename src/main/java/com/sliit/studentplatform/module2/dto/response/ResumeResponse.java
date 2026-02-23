package com.sliit.studentplatform.module2.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponse {
  private Long id;
  private Long userId;
  private String fileName;
  private String fileUrl;
  private Long fileSize;
  private boolean primary;
  private LocalDateTime createdAt;
}
