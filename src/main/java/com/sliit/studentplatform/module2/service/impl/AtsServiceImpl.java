package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module2.dto.response.AtsScoreResponse;
import com.sliit.studentplatform.module2.entity.AtsAnalysis;
import com.sliit.studentplatform.module2.entity.Resume;
import com.sliit.studentplatform.module2.repository.AtsAnalysisRepository;
import com.sliit.studentplatform.module2.repository.JobListingRepository;
import com.sliit.studentplatform.module2.repository.ResumeRepository;
import com.sliit.studentplatform.module2.service.interfaces.IAtsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IAtsService}.
 *
 * <p>
 * Uses Spring AI (GPT-4) to parse the CV text and compute an ATS score
 * against a job listing's required keywords.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AtsServiceImpl implements IAtsService {

  private final AtsAnalysisRepository atsAnalysisRepository;
  private final ResumeRepository resumeRepository;
  private final JobListingRepository jobListingRepository;
  private final ChatClient chatClient;

  @Override
  @Transactional
  public AtsScoreResponse analyzeResume(Long resumeId, Long jobListingId, Long userId) {
    log.info("Running ATS analysis: resume={}, job={}", resumeId, jobListingId);

    Resume resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", resumeId));

    var jobListing = jobListingRepository.findById(jobListingId)
        .orElseThrow(() -> new ResourceNotFoundException("JobListing", "id", jobListingId));

    String[] requiredKeywords = jobListing.getRequiredSkills() != null
        ? jobListing.getRequiredSkills()
        : new String[0];

    // TODO: integrate full text extraction from resume file in prod
    String resumeText = resume.getExtractedText() != null ? resume.getExtractedText() : "";

    // Simple keyword intersection matching
    String normalizedText = resumeText.toLowerCase();
    List<String> matched = Arrays.stream(requiredKeywords)
        .filter(k -> normalizedText.contains(k.toLowerCase()))
        .collect(Collectors.toList());
    List<String> missing = Arrays.stream(requiredKeywords)
        .filter(k -> !normalizedText.contains(k.toLowerCase()))
        .collect(Collectors.toList());

    double score = requiredKeywords.length == 0 ? 100.0
        : ((double) matched.size() / requiredKeywords.length) * 100;

    // Build GPT-4 prompt for qualitative feedback
    String prompt = String.format(
        "You are an ATS expert. Analyse this CV text against the job requirements and give concise improvement feedback.\n"
            +
            "Job required skills: %s\n" +
            "Missing skills: %s\n" +
            "CV text (excerpt): %s\n" +
            "Provide 3 specific, actionable recommendations.",
        String.join(", ", requiredKeywords),
        String.join(", ", missing),
        resumeText.substring(0, Math.min(resumeText.length(), 500)));

    String aiFeedback;
    try {
      aiFeedback = chatClient.prompt(prompt).call().content();
    } catch (Exception e) {
      log.error("AI feedback generation failed: {}", e.getMessage());
      aiFeedback = "AI feedback unavailable — please try again later.";
    }

    AtsAnalysis analysis = AtsAnalysis.builder()
        .resume(resume)
        .jobListing(jobListing)
        .atsScore(Math.round(score * 10.0) / 10.0)
        .keywordMatches(matched.toArray(new String[0]))
        .missingKeywords(missing.toArray(new String[0]))
        .aiFeedback(aiFeedback)
        .build();

    analysis = atsAnalysisRepository.save(analysis);

    return AtsScoreResponse.builder()
        .analysisId(analysis.getId())
        .resumeId(resumeId)
        .jobListingId(jobListingId)
        .atsScore(analysis.getAtsScore())
        .keywordMatches(analysis.getKeywordMatches())
        .missingKeywords(analysis.getMissingKeywords())
        .aiFeedback(analysis.getAiFeedback())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<AtsScoreResponse> getAnalysisHistory(Long userId) {
    log.info("Fetching ATS analysis history for user: {}", userId);
    return atsAnalysisRepository.findByResumeUserIdOrderByCreatedAtDesc(userId).stream()
        .map(a -> AtsScoreResponse.builder()
            .analysisId(a.getId())
            .resumeId(a.getResume().getId())
            .jobListingId(a.getJobListing() != null ? a.getJobListing().getId() : null)
            .atsScore(a.getAtsScore())
            .keywordMatches(a.getKeywordMatches())
            .missingKeywords(a.getMissingKeywords())
            .aiFeedback(a.getAiFeedback())
            .build())
        .collect(Collectors.toList());
  }
}
