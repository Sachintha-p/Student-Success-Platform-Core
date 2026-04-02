package com.sliit.studentplatform.module3.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneTimelineResponse {
  private List<MilestoneResponse> milestones;
  private LocalDate projectStartDate;
  private LocalDate projectEndDate;
  private String projectName;
}
