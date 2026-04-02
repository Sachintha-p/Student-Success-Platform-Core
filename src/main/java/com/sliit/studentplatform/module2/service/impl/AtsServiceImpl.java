package com.sliit.studentplatform.module2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.studentplatform.module2.entity.AtsAnalysis;
import com.sliit.studentplatform.module2.repository.AtsAnalysisRepository;
import com.sliit.studentplatform.module2.service.interfaces.IAtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AtsServiceImpl implements IAtsService {

  private final OpenAiChatModel chatModel;
  private final ObjectMapper objectMapper;
  private final AtsAnalysisRepository atsAnalysisRepository;

  @Override
  public Map<String, Object> analyzeResume(String resumeText, Long resumeId) {
    String prompt = "Act as a professional ATS. Analyze the following resume text and provide analysis in JSON format ONLY. " +
            "Do not include any explanation. JSON structure: { \"atsScore\": int, \"matchedKeywords\": [], \"missingKeywords\": [], \"weakPoints\": [], \"improvements\": [] } " +
            "Resume Text: " + resumeText;

    try {
      // 1. Call OpenAI
      String aiRawResponse = chatModel.call(prompt);

      // 2. CLEAN JSON (Fixes the code 96 / backtick error)
      String cleanedJson = cleanAiJson(aiRawResponse);

      // 3. Parse to Map
      Map<String, Object> results = objectMapper.readValue(cleanedJson, Map.class);

      // 4. Save to Database
      AtsAnalysis analysis = AtsAnalysis.builder()
              .resumeId(resumeId)
              .atsScore(results.get("atsScore") instanceof Number ? ((Number) results.get("atsScore")).intValue() : 0)
              .weakPoints(results.get("weakPoints").toString())
              .improvements(results.get("improvements").toString())
              .build();

      atsAnalysisRepository.save(analysis);
      return results;

    } catch (Exception e) {
      e.printStackTrace();
      Map<String, Object> errorMap = new HashMap<>();
      errorMap.put("error", "AI Scan failed: " + e.getMessage());
      return errorMap;
    }
  }

  @Override
  public List<AtsAnalysis> getAnalysisHistory(Long resumeId) {
    return atsAnalysisRepository.findByResumeIdOrderByCreatedAtDesc(resumeId);
  }

  /**
   * Helper to remove Markdown formatting like ```json ... ```
   */
  private String cleanAiJson(String raw) {
    if (raw == null) return "{}";
    String cleaned = raw.trim();
    if (cleaned.startsWith("```")) {
      cleaned = cleaned.replaceAll("^```[a-z]*\\n", "").replaceAll("\\n```$", "");
    }
    return cleaned.trim();
  }
}