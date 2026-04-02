package com.sliit.studentplatform.module2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.studentplatform.module2.entity.AtsAnalysis;
import com.sliit.studentplatform.module2.repository.AtsAnalysisRepository;
import com.sliit.studentplatform.module2.service.interfaces.IAtsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AtsServiceImpl implements IAtsService {

  private final OpenAiChatModel chatModel;
  private final ObjectMapper objectMapper;
  private final AtsAnalysisRepository atsAnalysisRepository;
  private final ResumeRepository resumeRepository;
  private final JobListingRepository jobListingRepository;
  private final Optional<ChatClient> chatClient;

  public AtsServiceImpl(AtsAnalysisRepository atsAnalysisRepository,
      ResumeRepository resumeRepository,
      JobListingRepository jobListingRepository,
      @Autowired(required = false) ChatClient chatClient) {
    this.atsAnalysisRepository = atsAnalysisRepository;
    this.resumeRepository = resumeRepository;
    this.jobListingRepository = jobListingRepository;
    this.chatClient = Optional.ofNullable(chatClient);
  }

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

    String aiFeedback;
    if (chatClient.isPresent()) {
      try {
        aiFeedback = chatClient.get().prompt().user(prompt).call().content();
      } catch (Exception e) {
        log.error("AI feedback generation failed: {}", e.getMessage());
        aiFeedback = "AI feedback unavailable — please try again later.";
      }
    } else {
      aiFeedback = "AI feedback unavailable — AI service not configured.";
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