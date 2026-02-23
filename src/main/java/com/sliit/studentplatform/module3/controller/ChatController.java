package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.entity.GroupChatMessage;
import com.sliit.studentplatform.module3.service.interfaces.IChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Group Chat")
public class ChatController {
  private final IChatService chatService;

  @PostMapping("/group/{groupId}")
  public ResponseEntity<ApiResponse<GroupChatMessage>> send(@PathVariable Long groupId,
      @RequestParam String content, @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponse.success(chatService.sendMessage(groupId, user.getId(), content), "Message sent"));
  }

  @GetMapping("/group/{groupId}")
  public ResponseEntity<ApiResponse<PagedResponse<GroupChatMessage>>> getMessages(
      @PathVariable Long groupId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    return ResponseEntity
        .ok(ApiResponse.success(chatService.getMessages(groupId, PageRequest.of(page, size)), "Messages retrieved"));
  }

  @DeleteMapping("/messages/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
    chatService.deleteMessage(id, user.getId());
    return ResponseEntity.ok(ApiResponse.success("Message deleted"));
  }
}
