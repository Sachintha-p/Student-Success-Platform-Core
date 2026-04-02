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
public class AllProjectsMilestonesResponse {
    private List<MilestoneTimelineResponse> projectTimelines;
    private int totalProjects;
    private int totalMilestones;
}
