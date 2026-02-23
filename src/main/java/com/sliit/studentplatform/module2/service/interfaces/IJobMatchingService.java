package com.sliit.studentplatform.module2.service.interfaces;

import com.sliit.studentplatform.module2.dto.response.JobMatchResponse;
import java.util.List;

public interface IJobMatchingService {
  List<JobMatchResponse> findMatchingJobsForUser(Long userId);

  void refreshMatchesForUser(Long userId);
}
