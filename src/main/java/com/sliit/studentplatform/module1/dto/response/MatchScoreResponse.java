package com.sliit.studentplatform.module1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response DTO containing a student's compatibility score with a group. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchScoreResponse {
  private Long studentId;
  private String studentName;
  private Long groupId;
  private String groupName;
  /** Compatibility score from 0 to 100. */
  private double score;
  private String[] matchedSkills;
  private String[] missingSkills;
}
