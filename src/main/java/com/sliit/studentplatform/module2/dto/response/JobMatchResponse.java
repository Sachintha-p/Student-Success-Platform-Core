package com.sliit.studentplatform.module2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMatchResponse {
  private Long id;
  private Long jobListingId;
  private String jobTitle;
  private String companyName;
  private String location;
  private String type;
  private Double matchScore;

  // AI Matchmaker Fields
  private String[] matchedSkills;
  private String[] missingSkills;
}