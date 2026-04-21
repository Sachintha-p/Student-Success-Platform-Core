package com.sliit.studentplatform.module4.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.module4.dto.request.AiQueryRequest;
import com.sliit.studentplatform.module4.dto.response.AiQueryResponse;
import com.sliit.studentplatform.module4.entity.ChatMessage;
import com.sliit.studentplatform.module4.entity.Conversation;
import com.sliit.studentplatform.module4.repository.ChatMessageRepository;
import com.sliit.studentplatform.module4.repository.ConversationRepository;
import com.sliit.studentplatform.module4.service.interfaces.IAiAssistantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sliit.studentplatform.auth.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai-assistant")
@RequiredArgsConstructor


@Tag(name = "AI Academic Assistant")
public class AiAssistantController {

  private final IAiAssistantService aiAssistantService;
  private final ChatMessageRepository chatMessageRepository;
  private final ConversationRepository conversationRepository;

  // ✅ ASK AI
  @PostMapping("/ask")
  public ResponseEntity<ApiResponse<AiQueryResponse>> ask(
          @Valid @RequestBody AiQueryRequest request,
          @RequestParam Long userId) {

    return ResponseEntity.ok(
            ApiResponse.success(
                    aiAssistantService.askQuestion(request, userId),
                    "Answer generated"
            )
    );
  }

  // 🔥 GET CHAT HISTORY
  @GetMapping("/conversation/{id}")
  public ResponseEntity<ApiResponse<List<ChatMessage>>> getConversation(
          @PathVariable Long id) {

    List<ChatMessage> messages = chatMessageRepository.findByConversationId(id);

    return ResponseEntity.ok(
            ApiResponse.success(messages, "Conversation fetched")
    );
  }

  // 🔥 GET USER CONVERSATIONS
  @GetMapping("/user/{userId}/conversations")
  public ResponseEntity<ApiResponse<List<Conversation>>> getUserConversations(
          @PathVariable Long userId) {

    List<Conversation> conversations =
            conversationRepository.findByUserIdAndActiveTrue(userId);

    return ResponseEntity.ok(
            ApiResponse.success(conversations, "User conversations fetched")
    );
  }

  // 🆕 CREATE CONVERSATION
  @PostMapping("/conversation/create")
  public ResponseEntity<ApiResponse<Conversation>> createConversation(
          @RequestParam Long userId) {

    User user = new User();
    user.setId(userId);

    Conversation conversation = new Conversation();
    conversation.setUser(user);
    conversation.setActive(true);

    Conversation saved = conversationRepository.save(conversation);

    return ResponseEntity.ok(
            ApiResponse.success(saved, "Conversation created")
    );
  }

  // 🆕 DEACTIVATE CONVERSATION
  @PutMapping("/conversation/{id}/deactivate")
  public ResponseEntity<ApiResponse<String>> deactivateConversation(
          @PathVariable Long id) {

    Conversation conversation = conversationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));

    conversation.setActive(false);
    conversationRepository.save(conversation);

    return ResponseEntity.ok(
            ApiResponse.success("Done", "Conversation deactivated")
    );
  }
}