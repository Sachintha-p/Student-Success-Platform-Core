package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.module3.dto.request.MilestoneRequest;
import com.sliit.studentplatform.module3.dto.response.AllProjectsMilestonesResponse;
import com.sliit.studentplatform.module3.dto.response.MilestoneProgressSummaryResponse;
import com.sliit.studentplatform.module3.dto.response.MilestoneResponse;
import com.sliit.studentplatform.module3.dto.response.MilestoneTimelineResponse;

import java.util.List;

public interface IMilestoneService {
  MilestoneResponse createMilestone(MilestoneRequest req, Long userId);
  MilestoneResponse updateMilestone(Long id, MilestoneRequest req, Long userId);
  void deleteMilestone(Long id, Long userId);
  MilestoneResponse getMilestoneById(Long id);
  List<MilestoneResponse> getMilestonesByProject(Long projectId);
  MilestoneResponse updateProgress(Long id, int progressPercentage, Long userId);
  void recalculateMilestoneProgress(Long milestoneId);
  MilestoneTimelineResponse getTimeline(Long projectId);
  MilestoneProgressSummaryResponse getProgressSummary(Long projectId);
  List<MilestoneResponse> getUpcomingDeadlines(Long projectId, int days);
  AllProjectsMilestonesResponse getAllProjectsTimeline();
}
