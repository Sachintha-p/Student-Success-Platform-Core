package com.sliit.studentplatform.module3.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneProgressSummaryResponse {
  private int totalMilestones;
  private int completedMilestones;
  private int overdueMilestones;
  private int upcomingMilestones;
  private double overallProgressPercentage;
  private List<MilestoneResponse> upcomingDeadlines;
}
