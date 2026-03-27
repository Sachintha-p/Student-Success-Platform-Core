package com.sliit.studentplatform.module4.service.impl;

import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module4.dto.request.AiQueryRequest;
import com.sliit.studentplatform.module4.dto.response.AiQueryResponse;
import com.sliit.studentplatform.module4.entity.ChatMessage;
import com.sliit.studentplatform.module4.entity.Conversation;
import com.sliit.studentplatform.module4.repository.ChatMessageRepository;
import com.sliit.studentplatform.module4.repository.ConversationRepository;
import com.sliit.studentplatform.module4.service.interfaces.IAiAssistantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of {@link IAiAssistantService}.
 *
 * <p>
 * Sends student queries to GPT-4 via Spring AI, stores the Q&A in the
 * conversation history, and returns a structured response.
 */
@Service
@Slf4j
public class AiAssistantServiceImpl implements IAiAssistantService {

  private final Optional<ChatClient> chatClient;
  private final ConversationRepository conversationRepository;
  private final ChatMessageRepository chatMessageRepository;

  public AiAssistantServiceImpl(@Autowired(required = false) ChatClient chatClient,
      ConversationRepository conversationRepository,
      ChatMessageRepository chatMessageRepository) {
    this.chatClient = Optional.ofNullable(chatClient);
    this.conversationRepository = conversationRepository;
    this.chatMessageRepository = chatMessageRepository;
  }

  @Override
  @Transactional
  public AiQueryResponse askQuestion(AiQueryRequest request, Long userId) {
    log.info("AI query from user {} in conversation {}", userId, request.getConversationId());

    Conversation conversation = conversationRepository.findById(request.getConversationId())
        .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", request.getConversationId()));

    // Save the user message
    ChatMessage userMessage = chatMessageRepository.save(ChatMessage.builder()
        .conversation(conversation)
        .role("USER")
        .content(request.getQuery())
        .build());

    // Build academic context prompt
    String systemPrompt = "You are an academic assistant for SLIIT university students. "
        + "Provide clear, accurate, and educational answers. Cite relevant concepts."
        + (request.getSubject() != null ? " The student is asking about subject: " + request.getSubject() + "." : "");

    String aiAnswer;
    if (chatClient.isPresent()) {
      try {
        aiAnswer = chatClient.get().prompt()
            .system(systemPrompt)
            .user(request.getQuery())
            .call()
            .content();
      } catch (Exception e) {
        log.error("GPT-4 call failed: {}", e.getMessage());
        aiAnswer = "I'm sorry, I couldn't process your question right now. Please try again later.";
      }
    } else {
      aiAnswer = "I'm sorry, the AI assistant is currently unavailable. Please try again later.";
    }

    // Save the assistant reply
    ChatMessage assistantMessage = chatMessageRepository.save(ChatMessage.builder()
        .conversation(conversation)
        .role("ASSISTANT")
        .content(aiAnswer)
        .build());

    return AiQueryResponse.builder()
        .conversationId(conversation.getId())
        .messageId(assistantMessage.getId())
        .answer(aiAnswer)
        .model("gpt-4")
        .timestamp(LocalDateTime.now())
        .build();
  }
}
