package com.sliit.studentplatform.module4.service;

import com.sliit.studentplatform.module4.dto.request.AiQueryRequest;
import com.sliit.studentplatform.module4.dto.response.AiQueryResponse;
import com.sliit.studentplatform.module4.entity.ChatMessage;
import com.sliit.studentplatform.module4.entity.Conversation;
import com.sliit.studentplatform.module4.repository.ChatMessageRepository;
import com.sliit.studentplatform.module4.repository.ConversationRepository;
import com.sliit.studentplatform.module4.service.impl.AiAssistantServiceImpl;
import com.sliit.studentplatform.auth.entity.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AiAssistantServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AiAssistantServiceImpl Unit Tests")
class AiAssistantServiceImplTest {

  @Mock
  private ChatClient chatClient;
  @Mock
  private ConversationRepository conversationRepository;
  @Mock
  private ChatMessageRepository chatMessageRepository;

  @InjectMocks
  private AiAssistantServiceImpl aiAssistantService;

  private Conversation conversation;
  private AiQueryRequest queryRequest;

  @BeforeEach
  void setUp() {
    User user = User.builder().id(1L).fullName("Eve Green").build();
    conversation = Conversation.builder().id(1L).user(user).title("Math Help").subject("Calculus").build();
    queryRequest = AiQueryRequest.builder().conversationId(1L).query("Explain integration by parts").subject("Calculus")
        .build();
  }

  @Test
  @DisplayName("askQuestion — should save user message, call GPT-4, and save assistant reply")
  void askQuestion_shouldSaveMessagesAndReturnAnswer() {
    // Arrange
    when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
    when(chatMessageRepository.save(argThat(m -> "USER".equals(m.getRole()))))
        .thenReturn(ChatMessage.builder().id(1L).role("USER").content(queryRequest.getQuery()).build());

    // Mock Spring AI fluent API
    ChatClient.ChatClientRequestSpec promptSpec = mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.CallResponseSpec callSpec = mock(ChatClient.CallResponseSpec.class);
    when(chatClient.prompt()).thenReturn(promptSpec);
    when(promptSpec.system(anyString())).thenReturn(promptSpec);
    when(promptSpec.user(anyString())).thenReturn(promptSpec);
    when(promptSpec.call()).thenReturn(callSpec);
    when(callSpec.content()).thenReturn("Integration by parts formula: ∫u dv = uv - ∫v du");

    ChatMessage assistantMsg = ChatMessage.builder().id(2L).role("ASSISTANT")
        .content("Integration by parts formula: ∫u dv = uv - ∫v du").build();
    when(chatMessageRepository.save(argThat(m -> "ASSISTANT".equals(m.getRole()))))
        .thenReturn(assistantMsg);

    // Act
    AiQueryResponse response = aiAssistantService.askQuestion(queryRequest, 1L);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getConversationId()).isEqualTo(1L);
    assertThat(response.getAnswer()).contains("∫u dv = uv");
    assertThat(response.getModel()).isEqualTo("gpt-4");

    verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
  }

  @Test
  @DisplayName("askQuestion — should return fallback message when AI service fails")
  void askQuestion_shouldReturnFallbackWhenAiFails() {
    // Arrange
    when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
    when(chatMessageRepository.save(argThat(m -> "USER".equals(m.getRole()))))
        .thenReturn(ChatMessage.builder().id(1L).role("USER").build());

    ChatClient.ChatClientRequestSpec promptSpec = mock(ChatClient.ChatClientRequestSpec.class);
    when(chatClient.prompt()).thenReturn(promptSpec);
    when(promptSpec.system(anyString())).thenReturn(promptSpec);
    when(promptSpec.user(anyString())).thenReturn(promptSpec);
    when(promptSpec.call()).thenThrow(new RuntimeException("Network error"));

    ChatMessage fallbackMsg = ChatMessage.builder().id(2L).role("ASSISTANT")
        .content("I'm sorry, I couldn't process your question right now. Please try again later.").build();
    when(chatMessageRepository.save(argThat(m -> "ASSISTANT".equals(m.getRole()))))
        .thenReturn(fallbackMsg);

    // Act
    AiQueryResponse response = aiAssistantService.askQuestion(queryRequest, 1L);

    // Assert
    assertThat(response.getAnswer()).contains("sorry");
  }

  @Test
  @DisplayName("askQuestion — should throw when conversation not found")
  void askQuestion_shouldThrowWhenConversationNotFound() {
    when(conversationRepository.findById(999L)).thenReturn(Optional.empty());
    queryRequest.setConversationId(999L);

    assertThatThrownBy(() -> aiAssistantService.askQuestion(queryRequest, 1L))
        .isInstanceOf(com.sliit.studentplatform.common.exception.ResourceNotFoundException.class);
  }
}
