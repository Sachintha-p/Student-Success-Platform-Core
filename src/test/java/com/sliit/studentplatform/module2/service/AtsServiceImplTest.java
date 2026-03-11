package com.sliit.studentplatform.module2.service;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.ai.service.AiMatchmakerService; // Added missing import
import com.sliit.studentplatform.module2.dto.response.AtsScoreResponse;
import com.sliit.studentplatform.module2.entity.AtsAnalysis;
import com.sliit.studentplatform.module2.entity.JobListing;
import com.sliit.studentplatform.module2.entity.Resume;
import com.sliit.studentplatform.module2.repository.AtsAnalysisRepository;
import com.sliit.studentplatform.module2.repository.JobListingRepository;
import com.sliit.studentplatform.module2.repository.ResumeRepository;
import com.sliit.studentplatform.module2.service.impl.AtsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtsServiceImpl Unit Tests")
class AtsServiceImplTest {

  @Mock
  private AtsAnalysisRepository atsAnalysisRepository;
  @Mock
  private ResumeRepository resumeRepository;
  @Mock
  private JobListingRepository jobListingRepository;
  @Mock
  private AiMatchmakerService aiMatchmakerService; // Changed from ChatClient to AiMatchmakerService

  @InjectMocks
  private AtsServiceImpl atsService;

  private User user;
  private Resume resume;
  private JobListing jobListing;

  @BeforeEach
  void setUp() {
    user = User.builder().id(1L).fullName("Bob Jones").build();
    resume = Resume.builder().id(10L).user(user).extractedText("Java Spring Boot").build();
    jobListing = JobListing.builder().id(20L).requiredSkills(new String[]{"Java", "AWS"}).build();
  }

  @Test
  @DisplayName("analyzeResume — should compute correct ATS score")
  void analyzeResume_shouldComputeCorrectScore() {
    when(resumeRepository.findById(10L)).thenReturn(Optional.of(resume));
    when(jobListingRepository.findById(20L)).thenReturn(Optional.of(jobListing));

    // Create mock response for the AI Service
    AtsScoreResponse mockAiResponse = AtsScoreResponse.builder()
            .atsScore(50.0)
            .aiFeedback("Good match")
            .keywordMatches(new String[]{"Java"})
            .missingKeywords(new String[]{"AWS"})
            .build();

    // Mock the new service call
    when(aiMatchmakerService.calculateAtsScore(anyString(), anyString())).thenReturn(mockAiResponse);

    AtsAnalysis saved = AtsAnalysis.builder()
            .id(100L)
            .resume(resume)
            .jobListing(jobListing)
            .atsScore(50.0)
            .aiFeedback("Feedback")
            .build();

    when(atsAnalysisRepository.save(any(AtsAnalysis.class))).thenReturn(saved);

    AtsScoreResponse response = atsService.analyzeResume(10L, 20L, 1L);
    assertThat(response.getAtsScore()).isEqualTo(50.0);
  }

  @Test
  @DisplayName("analyzeResume — should handle AI call failures gracefully")
  void analyzeResume_shouldHandleAiFails() {
    when(resumeRepository.findById(10L)).thenReturn(Optional.of(resume));
    when(jobListingRepository.findById(20L)).thenReturn(Optional.of(jobListing));

    // Simulate AI Service failure
    when(aiMatchmakerService.calculateAtsScore(anyString(), anyString()))
            .thenThrow(new RuntimeException("AI fail"));

    AtsAnalysis saved = AtsAnalysis.builder()
            .id(101L)
            .resume(resume)
            .jobListing(jobListing)
            .atsScore(0.0)
            .aiFeedback("AI feedback unavailable")
            .build();

    when(atsAnalysisRepository.save(any(AtsAnalysis.class))).thenReturn(saved);

    AtsScoreResponse response = atsService.analyzeResume(10L, 20L, 1L);
    assertThat(response.getAiFeedback()).contains("unavailable");
  }

  @Test
  @DisplayName("getAnalysisHistory — should return all history for a user")
  void getAnalysisHistory_shouldReturnAllRecords() {
    AtsAnalysis analysis = AtsAnalysis.builder().id(50L).resume(resume).atsScore(80.0).build();
    when(atsAnalysisRepository.findByResumeUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(analysis));

    var history = atsService.getAnalysisHistory(1L);
    assertThat(history).hasSize(1);
    assertThat(history.get(0).getAtsScore()).isEqualTo(80.0);
  }
}