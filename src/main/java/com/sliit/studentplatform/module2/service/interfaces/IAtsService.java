package com.sliit.studentplatform.module2.service.interfaces;

import com.sliit.studentplatform.module2.dto.response.AtsScoreResponse;
import java.util.List;

public interface IAtsService {
  AtsScoreResponse analyzeResume(Long resumeId, Long jobListingId, Long userId);

  List<AtsScoreResponse> getAnalysisHistory(Long userId);
}
