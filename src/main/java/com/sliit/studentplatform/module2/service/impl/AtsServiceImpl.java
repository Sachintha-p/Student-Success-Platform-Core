package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.ai.service.AiMatchmakerService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtsServiceImpl implements IAtsService {

  private final AtsAnalysisRepository atsAnalysisRepository;
  private final ResumeRepository resumeRepository;
  private final JobListingRepository jobListingRepository;
  private final AiMatchmakerService aiMatchmakerService; // Injected our AI Service

  @Override
  @Transactional
  public AtsScoreResponse analyzeResume(Long resumeId, Long jobListingId, Long userId) {
    log.info("Starting production ATS analysis for resume ID: {} and job ID: {}", resumeId, jobListingId);

    // 1. Fetch existing entities from Neon
    Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", resumeId));

    var jobListing = jobListingRepository.findById(jobListingId)
            .orElseThrow(() -> new ResourceNotFoundException("JobListing", "id", jobListingId));

    // 2. Prepare text for AI (uses extracted text if available)
    String resumeText = resume.getExtractedText() != null ? resume.getExtractedText() : "";
    String jobDescription = jobListing.getDescription();

    // 3. CALL THE AI BRAIN (This is the missing step!)
    AtsScoreResponse aiResult = aiMatchmakerService.calculateAtsScore(resumeText, jobDescription);

    // 4. PERSIST TO NEON
    AtsAnalysis analysis = AtsAnalysis.builder()
            .resume(resume)
            .jobListing(jobListing)
            .atsScore(aiResult.getAtsScore())
            .keywordMatches(aiResult.getKeywordMatches())
            .missingKeywords(aiResult.getMissingKeywords())
            .aiFeedback(aiResult.getAiFeedback())
            .build();

    analysis = atsAnalysisRepository.save(analysis); // Saves the record to database

    // 5. Return response with the actual database ID
    return AtsScoreResponse.builder()
            .analysisId(analysis.getId()) // No longer null!
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
    log.info("Fetching history for user: {}", userId);
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
            .collect(Collectors.toList()); //
  }
}