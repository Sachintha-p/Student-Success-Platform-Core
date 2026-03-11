package com.sliit.studentplatform.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    // This takes the auto-configured Google Gemini builder and creates a global ChatClient
    // that can be injected into AtsServiceImpl, AiMatchmakerService, or anywhere else!
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}