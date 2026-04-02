package com.sliit.studentplatform.ai.service;

import com.sliit.studentplatform.module2.dto.response.AtsScoreResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AiMatchmakerService {

    // Loads your API key from application.properties or application-dev.properties
    @Value("${openai.api.key:YOUR_FALLBACK_API_KEY}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    /**
     * Sends the CV and Job Description to OpenAI for ATS Analysis.
     */
    public AtsScoreResponse calculateAtsScore(String resumeText, String jobDescription) {
        try {
            System.out.println("🤖 Sending CV to OpenAI for ATS Analysis...");

            String prompt = String.format(
                    "You are an expert ATS. Job Description: %s. Candidate CV: %s. " +
                            "Return your evaluation strictly as a JSON object with: match_score (int 0-100), " +
                            "missing_keywords (array of 3-5 strings), and feedback (one actionable sentence).",
                    jobDescription, resumeText
            );

            JSONObject systemMessage = new JSONObject().put("role", "system").put("content", "You are a helpful ATS evaluation assistant designed to output strict JSON.");
            JSONObject userMessage = new JSONObject().put("role", "user").put("content", prompt);

            JSONObject requestBody = new JSONObject()
                    .put("model", "gpt-3.5-turbo")
                    .put("response_format", new JSONObject().put("type", "json_object"))
                    .put("messages", new JSONArray().put(systemMessage).put(userMessage))
                    .put("temperature", 0.2);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            String resultString = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            JSONObject resultJson = new JSONObject(resultString);

            // 1. Convert JSON Array to a Java String[] Array to perfectly match your DTO
            JSONArray keywordsArray = resultJson.getJSONArray("missing_keywords");
            String[] missingKeywordsArray = new String[keywordsArray.length()];
            for (int i = 0; i < keywordsArray.length(); i++) {
                missingKeywordsArray[i] = keywordsArray.getString(i);
            }

            // 2. Map the OpenAI JSON response directly to your Spring Boot DTO
            return AtsScoreResponse.builder()
                    .atsScore((double) resultJson.getInt("match_score")) // Converted int to Double
                    .missingKeywords(missingKeywordsArray)               // Passed the Array instead of a List
                    .aiFeedback(resultJson.getString("feedback"))
                    .keywordMatches(new String[0])                       // Passed an empty Array
                    .build();

        } catch (Exception e) {
            System.err.println("❌ OpenAI API Error: " + e.getMessage());
            // Safe fallback matching your DTO types so the app doesn't crash
            return AtsScoreResponse.builder()
                    .atsScore(0.0)
                    .missingKeywords(new String[]{"Error connecting to AI"})
                    .aiFeedback("Please try again later.")
                    .keywordMatches(new String[0])
                    .build();
        }
    }

    /**
     * Tests the connection to the OpenAI API to ensure the API key is working.
     */
    public String testAiConnection() {
        try {
            System.out.println("🤖 Pinging OpenAI API for connection test...");

            JSONObject userMessage = new JSONObject()
                    .put("role", "user")
                    .put("content", "Respond with exactly this text: '✅ OpenAI Connection Successful!'");

            JSONObject requestBody = new JSONObject()
                    .put("model", "gpt-3.5-turbo")
                    .put("messages", new JSONArray().put(userMessage))
                    .put("max_tokens", 15);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                return jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                return "❌ OpenAI Connection Failed. Status Code: " + response.statusCode() + " | Body: " + response.body();
            }

        } catch (Exception e) {
            return "❌ Failed to reach OpenAI API: " + e.getMessage();
        }
    }
}