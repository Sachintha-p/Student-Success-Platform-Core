package com.sliit.studentplatform.config;

import org.springframework.ai.chat.client.ChatClient;
<<<<<<< main
=======
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
>>>>>>> development
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(ChatClient.Builder.class)
public class AIConfig {

<<<<<<< main
    // This takes the auto-configured Google Gemini builder and creates a global ChatClient
    // that can be injected into AtsServiceImpl, AiMatchmakerService, or anywhere else!
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
=======
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        try {
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }
}
>>>>>>> development
