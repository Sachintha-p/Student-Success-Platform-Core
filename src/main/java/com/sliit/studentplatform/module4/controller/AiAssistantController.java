package com.sliit.studentplatform.module4.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module4.dto.request.AiQueryRequest;
import com.sliit.studentplatform.module4.dto.response.AiQueryResponse;
import com.sliit.studentplatform.module4.service.interfaces.IAiAssistantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai-assistant")
@RequiredArgsConstructor
@Tag(name = "AI Academic Assistant")
public class AiAssistantController {
  private final IAiAssistantService aiAssistantService;

  @PostMapping("/ask")
  public ResponseEntity<ApiResponse<AiQueryResponse>> ask(
      @Valid @RequestBody AiQueryRequest request,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity
        .ok(ApiResponse.success(aiAssistantService.askQuestion(request, user.getId()), "Answer generated"));
  }
}
