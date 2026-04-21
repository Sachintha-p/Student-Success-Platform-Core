package com.sliit.studentplatform.module4.service.impl;

import com.sliit.studentplatform.module4.dto.request.AiQueryRequest;
import com.sliit.studentplatform.module4.dto.response.AiQueryResponse;
import com.sliit.studentplatform.module4.dto.response.ResourceResponse;
import com.sliit.studentplatform.module4.entity.ChatMessage;
import com.sliit.studentplatform.module4.entity.Conversation;
import com.sliit.studentplatform.module4.repository.ConversationRepository;
import com.sliit.studentplatform.module4.repository.ChatMessageRepository;
import com.sliit.studentplatform.module4.service.interfaces.IAiAssistantService;
import com.sliit.studentplatform.module4.service.interfaces.IResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import java.util.List;

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
    private final UserRepository userRepository;
    private final IResourceService resourceService;

    public AiAssistantServiceImpl(
            @Autowired(required = false) ChatClient chatClient,
            ConversationRepository conversationRepository,
            ChatMessageRepository chatMessageRepository,
            UserRepository userRepository,
            IResourceService resourceService) {

        this.chatClient = Optional.ofNullable(chatClient);
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.resourceService = resourceService;
    }

    @Override
    @Transactional
    public AiQueryResponse askQuestion(AiQueryRequest request, Long userId) {

        // 1. Get REAL user or create mock
        User user = userRepository.findById(userId)
                .orElseGet(() -> {
                    User mockUser = new User();
                    mockUser.setFullName("Test Student");
                    mockUser.setEmail("test@sliit.lk");
                    mockUser.setPassword("password");
                    mockUser.setRole(com.sliit.studentplatform.common.enums.Role.STUDENT);
                    return userRepository.save(mockUser);
                });

        // 2. Get or create conversation
        Conversation conversation = conversationRepository
                .findById(request.getConversationId() != null ? request.getConversationId() : -1)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setUser(user);

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

        // 3. Build context
        List<ChatMessage> history = chatMessageRepository.findByConversationId(conversation.getId());
        StringBuilder context = new StringBuilder();

        history.stream()
                .skip(Math.max(0, history.size() - 10))
                .forEach(msg -> context.append(msg.getRole().toUpperCase())
                        .append(": ")
                        .append(msg.getContent())
                        .append("\n"));

        String systemMessage = "You are a helpful academic assistant for university students. Explain clearly and simply.\n"
                + "Previous Conversation:\n" + context;

        // 4. AI Response
        String aiAnswer;

        if (chatClient.isPresent()) {
            try {
                aiAnswer = chatClient.get()
                        .prompt()
                        .system(systemMessage) // ✅ FIXED (was systemPrompt)
                        .user(request.getQuery())
                        .call()
                        .content();
            } catch (Exception e) {
                log.error("GPT call failed: {}", e.getMessage());
                aiAnswer = "I'm sorry, I couldn't process your question right now. Please try again later.";
            }
        } else {
            aiAnswer = "I'm sorry, the AI assistant is currently unavailable.";
        }

        // 5. Save USER message
        ChatMessage userMessage = new ChatMessage();
        userMessage.setConversation(conversation);
        userMessage.setRole("user");
        userMessage.setContent(request.getQuery());
        chatMessageRepository.save(userMessage);

        // ✅ 6. SAVE AI MESSAGE (MISSING BEFORE)
        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setConversation(conversation);
        aiMessage.setRole("assistant");
        aiMessage.setContent(aiAnswer);
        chatMessageRepository.save(aiMessage);

        // 7. Recommendations
        String recommendationTopic =
                request.getSubject() != null ? request.getSubject() : request.getQuery();

        List<ResourceResponse> recommendations =
                resourceService.getAiRecommendations(recommendationTopic, userId);

        // 8. Final response
        return AiQueryResponse.builder()
                .conversationId(conversation.getId())
                .messageId(aiMessage.getId()) // ✅ FIXED
                .answer(aiAnswer)             // ✅ FIXED (was response)
                .timestamp(LocalDateTime.now())
                .recommendedResources(recommendations)
                .build();
    }
}