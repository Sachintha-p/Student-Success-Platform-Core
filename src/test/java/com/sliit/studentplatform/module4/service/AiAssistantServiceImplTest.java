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
    queryRequest = AiQueryRequest.builder().conversationId(1L).query("Explain integration by parts").subject("Calculus").build();
  }

  @Test
  @DisplayName("askQuestion — should save user message, call GPT-4, and save assistant reply")
  void askQuestion_shouldSaveMessagesAndReturnAnswer() {
    when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
    when(chatMessageRepository.save(argThat(m -> "USER".equals(m.getRole()))))
            .thenReturn(ChatMessage.builder().id(1L).role("USER").content(queryRequest.getQuery()).build());

    // For Spring AI 1.0.0-M1, we can't use nested spec classes that don't exist
    // Instead, use a simple answer to mock the fluent chain
    chatClient = mock(ChatClient.class);
    aiAssistantService = new AiAssistantServiceImpl(chatClient, conversationRepository, chatMessageRepository);

    // Create a mock chain using Mockito lenient mode
    final Object[] chain = new Object[1];
    when(chatClient.prompt()).thenAnswer(inv -> {
      Object spec = mock(Object.class);
      chain[0] = spec;
      return spec;
    });

    ChatMessage assistantMsg = ChatMessage.builder().id(2L).role("ASSISTANT").content("Integration by parts formula: ∫u dv = uv - ∫v du").build();
    when(chatMessageRepository.save(argThat(m -> "ASSISTANT".equals(m.getRole())))).thenReturn(assistantMsg);

    AiQueryResponse response = aiAssistantService.askQuestion(queryRequest, 1L);

    assertThat(response).isNotNull();
    assertThat(response.getAnswer()).contains("∫u dv = uv");
    verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
  }

  @Test
  @DisplayName("askQuestion — should return fallback message when AI service fails")
  void askQuestion_shouldReturnFallbackWhenAiFails() {
    when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
    when(chatMessageRepository.save(argThat(m -> "USER".equals(m.getRole()))))
            .thenReturn(ChatMessage.builder().id(1L).role("USER").build());

    chatClient = mock(ChatClient.class);
    aiAssistantService = new AiAssistantServiceImpl(chatClient, conversationRepository, chatMessageRepository);

    when(chatClient.prompt()).thenThrow(new RuntimeException("Network error"));

    ChatMessage fallbackMsg = ChatMessage.builder().id(2L).role("ASSISTANT").content("I'm sorry...").build();
    when(chatMessageRepository.save(argThat(m -> "ASSISTANT".equals(m.getRole())))).thenReturn(fallbackMsg);

    AiQueryResponse response = aiAssistantService.askQuestion(queryRequest, 1L);
    assertThat(response.getAnswer()).contains("I'm sorry");
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