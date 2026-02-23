package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.module3.dto.request.CreateMilestoneRequest;
import com.sliit.studentplatform.module3.dto.response.MilestoneResponse;
import java.util.List;

public interface IMilestoneService {
  MilestoneResponse createMilestone(CreateMilestoneRequest request, Long userId);

  MilestoneResponse getMilestone(Long id);

  List<MilestoneResponse> getMilestonesForGroup(Long groupId);

  MilestoneResponse updateMilestoneStatus(Long id, String status, Long userId);

  void deleteMilestone(Long id, Long userId);
}
