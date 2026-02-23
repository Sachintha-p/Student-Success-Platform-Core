package com.sliit.studentplatform.module4.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module4.dto.request.CreateConversationRequest;
import com.sliit.studentplatform.module4.dto.response.ConversationResponse;
import com.sliit.studentplatform.module4.service.interfaces.IConversationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
@Tag(name = "AI Conversations")
public class ConversationController {
  private final IConversationService conversationService;

  @PostMapping
  public ResponseEntity<ApiResponse<ConversationResponse>> create(@Valid @RequestBody CreateConversationRequest req,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponse.success(conversationService.createConversation(req, user.getId()), "Conversation created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ConversationResponse>>> myConversations(
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity
        .ok(ApiResponse.success(conversationService.getUserConversations(user.getId()), "Conversations retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ConversationResponse>> getById(@PathVariable Long id,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity
        .ok(ApiResponse.success(conversationService.getConversation(id, user.getId()), "Conversation retrieved"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
    conversationService.deleteConversation(id, user.getId());
    return ResponseEntity.ok(ApiResponse.success("Conversation deleted"));
  }
}
