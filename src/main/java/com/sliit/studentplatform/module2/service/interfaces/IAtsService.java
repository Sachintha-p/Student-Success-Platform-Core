package com.sliit.studentplatform.module2.service.interfaces;

import com.sliit.studentplatform.module2.entity.AtsAnalysis;
import java.util.List;
import java.util.Map;

public interface IAtsService {
  Map<String, Object> analyzeResume(String resumeText, Long resumeId);

  // FIX: Add this method for the test
  List<AtsAnalysis> getAnalysisHistory(Long resumeId);
}