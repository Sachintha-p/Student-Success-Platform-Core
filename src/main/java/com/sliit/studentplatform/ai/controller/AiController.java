package com.sliit.studentplatform.ai.controller;

<<<<<<< main
import com.sliit.studentplatform.ai.service.AiMatchmakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
=======
import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.module2.service.interfaces.PdfExtractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
>>>>>>> development

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

<<<<<<< main
    private final AiMatchmakerService aiMatchmakerService;

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        String aiResponse = aiMatchmakerService.testAiConnection();
        return ResponseEntity.ok(aiResponse);
=======
    private final PdfExtractionService pdfExtractionService;

    /**
     * Endpoint to analyze a CV using AI logic.
     * This method handles the IOException thrown by the PdfExtractionService.
     */
    @PostMapping("/analyze-resume")
    public ResponseEntity<ApiResponse<String>> analyzeResume(@RequestParam("file") MultipartFile file) {

        // Basic validation
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Please upload a valid PDF file."));
        }

        try {
            // Line 41: Extract text from the PDF
            // This is wrapped in a try-catch to satisfy the IOException requirement
            String extractedText = pdfExtractionService.extractTextFromPdf(file);

            // TODO: Add your AI processing logic here (e.g., calling an LLM service)

            // For now, we return the extracted text to verify success
            return ResponseEntity.ok(ApiResponse.success(extractedText, "AI analysis of resume completed successfully"));

        } catch (IOException e) {
            // Log the error for debugging
            e.printStackTrace();

            // Return a clean error response to the frontend
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process the resume for AI analysis: " + e.getMessage()));
        }
>>>>>>> development
    }
}