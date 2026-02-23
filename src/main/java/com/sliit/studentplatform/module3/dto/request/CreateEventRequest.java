package com.sliit.studentplatform.module3.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {

  @NotBlank
  @Size(max = 200)
  private String title;
  @Size(max = 3000)
  private String description;
  @NotNull
  @Future
  private LocalDateTime eventDate;
  @Size(max = 200)
  private String venue;
  private boolean online;
  @Min(1)
  @Max(10000)
  private Integer maxAttendees;
}
