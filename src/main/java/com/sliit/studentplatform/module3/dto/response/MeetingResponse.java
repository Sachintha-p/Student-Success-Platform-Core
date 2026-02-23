package com.sliit.studentplatform.module3.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingResponse {
  private Long id;
  private Long groupId;
  private String title;
  private String agenda;
  private LocalDateTime meetingTime;
  private Integer durationMinutes;
  private String meetingLink;
  private String status;
}
