package com.sliit.studentplatform.module3.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingResponse {
  private Long id;
  private Long groupId;
  private String title;
  private List<LocalDateTime> proposedDates;
  private LocalDateTime finalDate;
  private String location;
  private String meetingLink;
  private List<AvailabilitySummaryResponse> availabilitySummary;
  private Long createdById;
  private LocalDateTime createdAt;
}
