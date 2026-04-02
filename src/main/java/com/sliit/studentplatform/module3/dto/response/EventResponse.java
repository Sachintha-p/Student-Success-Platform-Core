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
  private String category;
  private Long organizerId;
  private Integer maxAttendees;
  private Boolean isOnline;
  private Boolean isPublished;
  private String createdBy;
  private LocalDateTime createdAt;
}
