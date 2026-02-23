package com.sliit.studentplatform.module2.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {

  @NotNull
  private Long jobListingId;

  private Long resumeId;

  @Size(max = 3000)
  private String coverLetter;
}
