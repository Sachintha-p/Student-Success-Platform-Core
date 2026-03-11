package com.sliit.studentplatform.ai.service;

import com.sliit.studentplatform.module2.dto.response.AtsScoreResponse;
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
        log.info("Testing Spring AI OpenAI connection...");
        return chatClient.prompt()
                .user("Hello! You are now connected to the SLIIT Student Hub backend via OpenAI.")
                .call()
                .content();
    }

    public AtsScoreResponse calculateAtsScore(String resumeText, String jobDescription) {
        log.info("Sending resume to OpenAI for ATS scoring...");

        String systemPrompt = """
            You are an expert IT recruiter. Evaluate the provided resume against the job description.
            Return the result in JSON format with exactly these fields:
            - atsScore (0.0 to 100.0)
            - keywordMatches (array of matching skills)
            - missingKeywords (array of missing required skills)
            - aiFeedback (concise advice for improvement)
            """;

        String userPrompt = String.format("Job Description: %s\n\nResume Text: %s", jobDescription, resumeText);

        try {
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .entity(AtsScoreResponse.class);
        } catch (Exception e) {
            log.error("AI Analysis failed", e);
            throw new RuntimeException("AI processing failed. Please check your OpenAI quota and key.");
        }
    }
}