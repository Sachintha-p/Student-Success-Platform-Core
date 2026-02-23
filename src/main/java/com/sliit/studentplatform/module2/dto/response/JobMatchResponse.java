package com.sliit.studentplatform.module2.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMatchResponse {
  private Long jobListingId;
  private String jobTitle;
  private String company;
  private Double matchScore;
  private String[] matchedSkills;
  private String[] missingSkills;
}
