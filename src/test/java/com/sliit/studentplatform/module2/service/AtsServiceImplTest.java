package com.sliit.studentplatform.module2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.studentplatform.module2.entity.AtsAnalysis;
import com.sliit.studentplatform.module2.repository.AtsAnalysisRepository;
import com.sliit.studentplatform.module2.service.impl.AtsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.openai.OpenAiChatModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AtsServiceImplTest {

  @Mock
  private OpenAiChatModel chatModel;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private AtsAnalysisRepository atsAnalysisRepository;

  @InjectMocks
  private AtsServiceImpl atsService;

  private final Long mockResumeId = 1L;
  private final String mockText = "Experienced Software Engineering student with Java, Spring Boot, and React skills.";
  private final String mockAiResponse = "{\"atsScore\": 85, \"matchedKeywords\": [\"Java\", \"Spring Boot\"], \"missingKeywords\": [\"Docker\"], \"weakPoints\": [\"Missing CI/CD\"], \"improvements\": [\"Add cloud deployment experience\"]}";
  private Map<String, Object> mockParsedResult;

  @BeforeEach
  void setUp() {
    mockParsedResult = new HashMap<>();
    mockParsedResult.put("atsScore", 85);
    mockParsedResult.put("matchedKeywords", Arrays.asList("Java", "Spring Boot"));
    mockParsedResult.put("missingKeywords", Arrays.asList("Docker"));
    mockParsedResult.put("weakPoints", Arrays.asList("Missing CI/CD"));
    mockParsedResult.put("improvements", Arrays.asList("Add cloud deployment experience"));
  }

  @Test
  void testAnalyzeResume_Success() throws Exception {
    // Arrange
    when(chatModel.call(any(String.class))).thenReturn(mockAiResponse);
    when(objectMapper.readValue(mockAiResponse, Map.class)).thenReturn(mockParsedResult);
    when(atsAnalysisRepository.save(any(AtsAnalysis.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    // FIX: Passing exactly 2 arguments as required by the updated service
    Map<String, Object> result = atsService.analyzeResume(mockText, mockResumeId);

    // Assert
    assertNotNull(result);
    assertEquals(85, result.get("atsScore"));
    verify(chatModel, times(1)).call(any(String.class));
    verify(atsAnalysisRepository, times(1)).save(any(AtsAnalysis.class));
  }

  @Test
  void testAnalyzeResume_FailureHandling() throws Exception {
    // Arrange
    when(chatModel.call(any(String.class))).thenThrow(new RuntimeException("OpenAI API Down"));

    // Act
    Map<String, Object> result = atsService.analyzeResume(mockText, mockResumeId);

    // Assert
    assertNotNull(result);
    assertTrue(result.containsKey("error"));
    assertTrue(result.get("error").toString().contains("OpenAI API Down"));
    verify(atsAnalysisRepository, never()).save(any(AtsAnalysis.class));
  }

  @Test
  void testGetAnalysisHistory_Success() {
    // Arrange
    // FIX: Using .resumeId() instead of .resume() because we updated the entity
    AtsAnalysis mockAnalysis1 = AtsAnalysis.builder()
            .id(100L)
            .resumeId(mockResumeId)
            .atsScore(80)
            .weakPoints("Needs more projects")
            .improvements("Add GitHub link")
            .build();

    AtsAnalysis mockAnalysis2 = AtsAnalysis.builder()
            .id(101L)
            .resumeId(mockResumeId)
            .atsScore(90)
            .weakPoints("[]")
            .improvements("[]")
            .build();

    when(atsAnalysisRepository.findByResumeId(mockResumeId)).thenReturn(Arrays.asList(mockAnalysis1, mockAnalysis2));

    // Act
    // FIX: Testing the newly added history method
    List<AtsAnalysis> history = atsService.getAnalysisHistory(mockResumeId);

    // Assert
    assertNotNull(history);
    assertEquals(2, history.size());
    assertEquals(80, history.get(0).getAtsScore());
    verify(atsAnalysisRepository, times(1)).findByResumeId(mockResumeId);
  }
}