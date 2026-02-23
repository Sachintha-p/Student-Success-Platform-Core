package com.sliit.studentplatform.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI configuration.
 *
 * <p>
 * Provides a {@link ChatClient} bean wired to the OpenAI GPT-4 model.
 * The API key and model settings are bound from application properties
 * ({@code spring.ai.openai.*}).
 */
@Configuration
public class AIConfig {

  /**
   * Creates a {@link ChatClient} using the auto-configured
   * {@link OpenAiChatModel}.
   *
   * @param chatModel the auto-configured OpenAI chat model
   * @return a ready-to-use ChatClient
   */
  @Bean
  public ChatClient chatClient(OpenAiChatModel chatModel) {
    return ChatClient.builder(chatModel).build();
  }
}
