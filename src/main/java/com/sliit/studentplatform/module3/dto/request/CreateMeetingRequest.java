package com.sliit.studentplatform.module3.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMeetingRequest {
  @NotNull
  private Long groupId;
  @NotBlank
  @Size(max = 200)
  private String title;
  @Size(max = 3000)
  private String agenda;
  @NotNull
  @Future
  private LocalDateTime meetingTime;
  @Min(15)
  @Max(480)
  private Integer durationMinutes;
  private String meetingLink;
}
