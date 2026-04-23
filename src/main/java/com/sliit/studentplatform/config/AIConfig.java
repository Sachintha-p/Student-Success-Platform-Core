package com.sliit.studentplatform.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(ChatClient.Builder.class)
public class AIConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        try {
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }
}
