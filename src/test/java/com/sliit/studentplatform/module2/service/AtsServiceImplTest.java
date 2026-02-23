package com.sliit.studentplatform.module2.service;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
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
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AtsServiceImpl}.
 */
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
  private ChatClient chatClient;

  @InjectMocks
  private AtsServiceImpl atsService;

  private User user;
  private Resume resume;
  private JobListing jobListing;

  @BeforeEach
  void setUp() {
    user = User.builder().id(1L).fullName("Bob Jones").email("bob@sliit.lk").build();

    resume = Resume.builder()
        .id(10L).user(user)
        .fileName("cv.pdf")
        .fileUrl("https://storage.example.com/cv.pdf")
        .extractedText("Java Spring Boot REST API microservices Docker")
        .build();

    jobListing = JobListing.builder()
        .id(20L)
        .title("Backend Developer")
        .company("TechCorp")
        .requiredSkills(new String[] { "Java", "Spring Boot", "AWS" })
        .build();
  }

  @Test
  @DisplayName("analyzeResume — should compute correct ATS score with matched/missing keywords")
  void analyzeResume_shouldComputeCorrectScore() {
    // Arrange
    when(resumeRepository.findById(10L)).thenReturn(Optional.of(resume));
    when(jobListingRepository.findById(20L)).thenReturn(Optional.of(jobListing));

    // Mock Spring AI call
    ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
    ChatClient.PromptSpec promptSpec = mock(ChatClient.PromptSpec.class);
    when(chatClient.prompt(any(String.class))).thenReturn(promptSpec);
    when(promptSpec.call()).thenReturn(callResponseSpec);
    when(callResponseSpec.content()).thenReturn("Improve your AWS experience section.");

    AtsAnalysis savedAnalysis = AtsAnalysis.builder()
        .id(100L).resume(resume).jobListing(jobListing)
        .atsScore(66.7).keywordMatches(new String[] { "Java", "Spring Boot" })
        .missingKeywords(new String[] { "AWS" }).aiFeedback("Improve your AWS experience section.")
        .build();
    when(atsAnalysisRepository.save(any(AtsAnalysis.class))).thenReturn(savedAnalysis);

    // Act
    AtsScoreResponse response = atsService.analyzeResume(10L, 20L, 1L);

    // Assert
    assertThat(response.getAtsScore()).isEqualTo(66.7);
    assertThat(response.getKeywordMatches()).contains("Java", "Spring Boot");
    assertThat(response.getMissingKeywords()).contains("AWS");
    assertThat(response.getAiFeedback()).isNotBlank();
  }

  @Test
  @DisplayName("analyzeResume — should degrade gracefully when AI call fails")
  void analyzeResume_shouldDegradesGracefullyWhenAiFails() {
    // Arrange
    when(resumeRepository.findById(10L)).thenReturn(Optional.of(resume));
    when(jobListingRepository.findById(20L)).thenReturn(Optional.of(jobListing));
    when(chatClient.prompt(any(String.class))).thenThrow(new RuntimeException("AI unavailable"));

    AtsAnalysis savedAnalysis = AtsAnalysis.builder()
        .id(101L).resume(resume).jobListing(jobListing)
        .atsScore(66.7).keywordMatches(new String[] { "Java", "Spring Boot" })
        .missingKeywords(new String[] { "AWS" }).aiFeedback("AI feedback unavailable — please try again later.")
        .build();
    when(atsAnalysisRepository.save(any(AtsAnalysis.class))).thenReturn(savedAnalysis);

    // Act
    AtsScoreResponse response = atsService.analyzeResume(10L, 20L, 1L);

    // Assert
    assertThat(response.getAiFeedback()).contains("unavailable");
  }

  @Test
  @DisplayName("getAnalysisHistory — should return all history for a user")
  void getAnalysisHistory_shouldReturnAllRecords() {
    // Arrange
    AtsAnalysis analysis = AtsAnalysis.builder()
        .id(50L).resume(resume).jobListing(jobListing)
        .atsScore(80.0).build();
    when(atsAnalysisRepository.findByResumeUserIdOrderByCreatedAtDesc(1L))
        .thenReturn(List.of(analysis));

    // Act
    var history = atsService.getAnalysisHistory(1L);

    // Assert
    assertThat(history).hasSize(1);
    assertThat(history.get(0).getAnalysisId()).isEqualTo(50L);
  }
}
