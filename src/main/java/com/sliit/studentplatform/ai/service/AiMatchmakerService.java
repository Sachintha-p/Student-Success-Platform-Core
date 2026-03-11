package com.sliit.studentplatform.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiMatchmakerService {

    private final ChatClient chatClient;

    public AiMatchmakerService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String testAiConnection() {
        log.info("Testing Spring AI Gemini connection...");
        return chatClient.prompt()
                .user("Hello Gemini! You are now connected to the SLIIT Student Hub backend. Please reply with a short, friendly greeting to the development team.")
                .call()
                .content();
    }
}