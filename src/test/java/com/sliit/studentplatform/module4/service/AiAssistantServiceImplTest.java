package com.sliit.studentplatform.module4.service;

import com.sliit.studentplatform.module4.dto.request.AiQueryRequest;
import com.sliit.studentplatform.module4.dto.response.AiQueryResponse;
import com.sliit.studentplatform.module4.entity.ChatMessage;
import com.sliit.studentplatform.module4.entity.Conversation;
import com.sliit.studentplatform.module4.repository.ChatMessageRepository;
import com.sliit.studentplatform.module4.repository.ConversationRepository;
import com.sliit.studentplatform.module4.service.impl.AiAssistantServiceImpl;
import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.module4.service.interfaces.IResourceService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Optional;
import java.util.ArrayList;

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
  @Mock
  private UserRepository userRepository;
  @Mock
  private IResourceService resourceService;

  @InjectMocks
  private AiAssistantServiceImpl aiAssistantService;

  private User user;
  private Conversation conversation;
  private AiQueryRequest queryRequest;

  @BeforeEach
  void setUp() {
    user = User.builder().id(12L).fullName("Eve Green").build();
    conversation = Conversation.builder().id(1L).user(user).title("Math Help").subject("Calculus").build();
    queryRequest = AiQueryRequest.builder().conversationId(1L).query("Explain integration by parts").subject("Calculus").build();
  }

  @Test
  @DisplayName("askQuestion — should save messages and return AI answer")
  void askQuestion_shouldSaveMessagesAndReturnAnswer() {
    // Setup
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(conversationRepository.findById(anyLong())).thenReturn(Optional.of(conversation));
    when(chatMessageRepository.findByConversationId(anyLong())).thenReturn(new ArrayList<>());
    
    // Mock ChatClient Fluent API
    ChatClient.ChatClientRequestSpec spec = mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.CallResponseSpec callSpec = mock(ChatClient.CallResponseSpec.class);
    
    when(chatClient.prompt()).thenReturn(spec);
    when(spec.system(anyString())).thenReturn(spec);
    when(spec.user(anyString())).thenReturn(spec);
    when(spec.call()).thenReturn(callSpec);
    when(callSpec.content()).thenReturn("AI Answer Content");

    when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());
    when(resourceService.getAiRecommendations(anyString(), anyLong())).thenReturn(new ArrayList<>());

    // Execute
    AiQueryResponse response = aiAssistantService.askQuestion(queryRequest, 12L);

    // Verify
    assertThat(response).isNotNull();
    assertThat(response.getAnswer()).isEqualTo("AI Answer Content");
    verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
  }

  @Test
  @DisplayName("askQuestion — should return fallback response on AI service failure")
  void askQuestion_shouldHandleAiFailure() {
    // Setup
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(conversationRepository.findById(anyLong())).thenReturn(Optional.of(conversation));
    when(chatMessageRepository.findByConversationId(anyLong())).thenReturn(new ArrayList<>());
    when(chatClient.prompt()).thenThrow(new RuntimeException("AI Service Down"));
    when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());
    when(resourceService.getAiRecommendations(anyString(), anyLong())).thenReturn(new ArrayList<>());

    // Execute
    AiQueryResponse response = aiAssistantService.askQuestion(queryRequest, 12L);

    // Verify — service catches the error and returns a graceful fallback
    assertThat(response).isNotNull();
    assertThat(response.getAnswer()).contains("couldn't process");
  }
}