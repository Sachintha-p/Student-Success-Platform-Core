package com.sliit.studentplatform.ai.controller;

import com.sliit.studentplatform.ai.service.AiMatchmakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiMatchmakerService aiMatchmakerService;

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        String aiResponse = aiMatchmakerService.testAiConnection();
        return ResponseEntity.ok(aiResponse);
    }
}