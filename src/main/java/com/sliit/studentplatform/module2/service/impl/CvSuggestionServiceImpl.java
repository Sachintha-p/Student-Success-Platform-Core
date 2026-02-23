package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module2.dto.response.CvSuggestionResponse;
import com.sliit.studentplatform.module2.entity.CvSuggestion;
import com.sliit.studentplatform.module2.entity.Resume;
import com.sliit.studentplatform.module2.repository.CvSuggestionRepository;
import com.sliit.studentplatform.module2.repository.ResumeRepository;
import com.sliit.studentplatform.module2.service.interfaces.ICvSuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ICvSuggestionService}.
 *
 * <p>
 * Uses Spring AI GPT-4 to generate targeted CV improvement suggestions
 * per section (Summary, Experience, Skills, Education).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CvSuggestionServiceImpl implements ICvSuggestionService {

  private final CvSuggestionRepository cvSuggestionRepository;
  private final ResumeRepository resumeRepository;
  private final ChatClient chatClient;

  @Override
  @Transactional
  public List<CvSuggestionResponse> generateSuggestions(Long resumeId, Long userId) {
    log.info("Generating CV suggestions for resume id: {}", resumeId);

    Resume resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", resumeId));

    String resumeText = resume.getExtractedText() != null ? resume.getExtractedText() : "No text available";

    String prompt = String.format(
        "You are a professional CV coach. Analyse the following CV text and provide 5 specific, "
            + "actionable improvement suggestions. For each suggestion output JSON: "
            + "{\"section\":\"...\", \"suggestion\":\"...\", \"priority\":\"HIGH|MEDIUM|LOW\"}\n"
            + "CV text:\n%s\n"
            + "Return only a JSON array of suggestion objects.",
        resumeText.substring(0, Math.min(resumeText.length(), 2000)));

    String aiResponse;
    try {
      aiResponse = chatClient.prompt(prompt).call().content();
    } catch (Exception e) {
      log.error("GPT-4 CV suggestion generation failed: {}", e.getMessage());
      aiResponse = "[]";
    }

    // TODO: Parse JSON array response properly with ObjectMapper
    // For now, create a placeholder single suggestion
    CvSuggestion placeholder = CvSuggestion.builder()
        .resume(resume)
        .section("General")
        .suggestion(aiResponse)
        .priority("HIGH")
        .applied(false)
        .build();
    cvSuggestionRepository.save(placeholder);

    return getSuggestionsForResume(resumeId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CvSuggestionResponse> getSuggestionsForResume(Long resumeId) {
    return cvSuggestionRepository.findByResumeIdAndAppliedFalse(resumeId)
        .stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public CvSuggestionResponse markApplied(Long suggestionId, Long userId) {
    log.info("Marking suggestion {} as applied", suggestionId);
    CvSuggestion suggestion = cvSuggestionRepository.findById(suggestionId)
        .orElseThrow(() -> new ResourceNotFoundException("CvSuggestion", "id", suggestionId));
    suggestion.setApplied(true);
    return mapToResponse(cvSuggestionRepository.save(suggestion));
  }

  private CvSuggestionResponse mapToResponse(CvSuggestion s) {
    return CvSuggestionResponse.builder()
        .id(s.getId()).resumeId(s.getResume().getId())
        .section(s.getSection()).suggestion(s.getSuggestion())
        .priority(s.getPriority()).applied(s.isApplied()).build();
  }
}
