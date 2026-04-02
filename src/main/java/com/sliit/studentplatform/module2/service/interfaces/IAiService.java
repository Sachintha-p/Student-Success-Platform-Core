package com.sliit.studentplatform.module2.service.interfaces;

import java.util.Map;

public interface IAiService {
    // This method will take raw text and return a structured Map of results
    Map<String, Object> analyzeResumeWithAi(String resumeText);
}