package com.sliit.studentplatform.module3.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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
  
  @NotEmpty
  private List<LocalDateTime> proposedDates;
  
  private String location;
  
  private String meetingLink;
}
