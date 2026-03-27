package com.sliit.studentplatform.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AiMatchmakerService {
    private final Optional<ChatClient> chatClient;

    public AiMatchmakerService(@Autowired(required = false) ChatClient chatClient) {
        this.chatClient = Optional.ofNullable(chatClient);
    }

    public String testAiConnection() {
        log.info("Testing Spring AI Gemini connection...");
        return chatClient.map(client -> client.prompt()
                .user("Hello Gemini! You are now connected to the SLIIT Student Hub backend. Please reply with a short, friendly greeting to the development team.")
                .call()
                .content()).orElse("AI Service not available");
    }
}