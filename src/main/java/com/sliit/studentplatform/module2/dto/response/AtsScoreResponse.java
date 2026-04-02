package com.sliit.studentplatform.module2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsScoreResponse {
  private Long analysisId;
  private Long resumeId;
  private Long jobListingId;
  private Double atsScore;
  private String[] keywordMatches;
  private String[] missingKeywords;
  private String aiFeedback;
}