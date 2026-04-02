package com.sliit.studentplatform.module4.service.impl;

import com.sliit.studentplatform.module4.dto.request.AiQueryRequest;
import com.sliit.studentplatform.module4.dto.response.AiQueryResponse;
import com.sliit.studentplatform.module4.entity.ChatMessage;
import com.sliit.studentplatform.module4.entity.Conversation;
import com.sliit.studentplatform.module4.repository.ConversationRepository;
import com.sliit.studentplatform.module4.repository.ChatMessageRepository;
import com.sliit.studentplatform.module4.service.interfaces.IAiAssistantService;
import com.sliit.studentplatform.module4.service.interfaces.IResourceService;
import com.sliit.studentplatform.module4.dto.response.ResourceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiAssistantServiceImpl implements IAiAssistantService {

  private final ChatClient chatClient;
  private final ConversationRepository conversationRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserRepository userRepository;
  private final IResourceService resourceService;

  @Override
  @Transactional
  public AiQueryResponse askQuestion(AiQueryRequest request, Long userId) {

    // 1. Get REAL user or use a Mock one for testing
    User user = userRepository.findById(userId)
            .orElseGet(() -> {
                User mockUser = new User();
                mockUser.setFullName("Test Student");
                mockUser.setEmail("test@sliit.lk");
                mockUser.setPassword("password"); // Required field
                mockUser.setRole(com.sliit.studentplatform.common.enums.Role.STUDENT); // Required field
                return userRepository.save(mockUser);
            });

    // 2. Get or create conversation
    Conversation conversation = conversationRepository
            .findById(request.getConversationId() != null ? request.getConversationId() : -1)
            .orElseGet(() -> {
              Conversation newConv = new Conversation();
              newConv.setUser(user);
              
              // Extract a meaningful title (first 5-6 words or 40 chars)
              String[] words = request.getQuery().split("\\s+");
              StringBuilder sb = new StringBuilder();
              for (int i = 0; i < Math.min(words.length, 6); i++) {
                sb.append(words[i]).append(" ");
              }
              String title = sb.toString().trim();
              if (title.length() > 40) title = title.substring(0, 37) + "...";
              else if (words.length > 6) title += "...";
              
              newConv.setTitle(title);
              newConv.setActive(true);
              return conversationRepository.save(newConv);
            });

    // 3. Get History for context-awareness (Last 10 messages)
    List<ChatMessage> history = chatMessageRepository.findByConversationId(conversation.getId());
    StringBuilder context = new StringBuilder();
    history.stream().skip(Math.max(0, history.size() - 10)).forEach(msg -> {
      context.append(msg.getRole().toUpperCase()).append(": ").append(msg.getContent()).append("\n");
    });

    // 4. Generate AI response
    String systemMessage = "You are a helpful academic assistant for university students. Explain clearly and simply.\n" +
            "Previous Conversation:\n" + context.toString();

    String response = chatClient.prompt()
            .system(systemMessage)
            .user(request.getQuery())
            .call()
            .content();

    // 5. Save user message
    ChatMessage userMessage = new ChatMessage();
    userMessage.setConversation(conversation);
    userMessage.setRole("user");
    userMessage.setContent(request.getQuery());
    chatMessageRepository.save(userMessage);

    // 6. Save AI message
    ChatMessage aiMessage = new ChatMessage();
    aiMessage.setConversation(conversation);
    aiMessage.setRole("assistant");
    aiMessage.setContent(response);
    chatMessageRepository.save(aiMessage);

    // 7. Get recommended resources based on query and response
    String recommendationTopic = request.getSubject() != null ? request.getSubject() : request.getQuery();
    List<ResourceResponse> recommendations = resourceService.getAiRecommendations(recommendationTopic, userId);

    // 8. Final Response
    return AiQueryResponse.builder()
            .conversationId(conversation.getId())
            .messageId(aiMessage.getId())
            .answer(response)
            .timestamp(java.time.LocalDateTime.now())
            .recommendedResources(recommendations)
            .build();
  }
}