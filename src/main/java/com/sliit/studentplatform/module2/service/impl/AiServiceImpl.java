package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.module2.service.interfaces.IAiService;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements IAiService {

    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Override
    public Map<String, Object> analyzeResumeWithAi(String resumeText) {
        // 1. Create the AI Prompt
        String prompt = "Act as a professional ATS (Applicant Tracking System). " +
                "Analyze the following resume text and provide a detailed analysis in JSON format ONLY. " +
                "Evaluate it based on current Software Engineering industry standards. " +
                "JSON structure: { \"atsScore\": int, \"matchedKeywords\": [], \"missingKeywords\": [], \"weakPoints\": [], \"improvements\": [] } " +
                "Resume Text: " + resumeText;

        try {
            // 2. Call OpenAI (using gpt-4o-mini as per your properties)
            String aiResponseText = chatModel.call(prompt);

            // 3. Parse the JSON response directly into a Map
            return objectMapper.readValue(aiResponseText, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "AI Analysis failed: " + e.getMessage());
        }
    }
}