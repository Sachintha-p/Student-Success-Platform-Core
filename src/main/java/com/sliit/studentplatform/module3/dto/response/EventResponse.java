package com.sliit.studentplatform.module3.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
  private Long id;
  private String title;
  private String description;
  private LocalDateTime eventDate;
  private String venue;
  private boolean online;
  private Integer maxAttendees;
  private int rsvpCount;
  private Long organizerId;
  private String organizerName;
  private boolean published;
  private LocalDateTime createdAt;
}
