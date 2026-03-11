package com.sliit.studentplatform.ai.controller;

import com.sliit.studentplatform.ai.service.AiMatchmakerService;
import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.module2.dto.response.AtsScoreResponse;
import com.sliit.studentplatform.module2.service.interfaces.IAtsService;
import com.sliit.studentplatform.module2.service.interfaces.PdfExtractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiMatchmakerService aiMatchmakerService;
    private final PdfExtractionService pdfExtractionService;
    private final IAtsService atsService; // Injected to handle database persistence

    /**
     * Test endpoint to verify AI connection (Gemini/OpenAI).
     */
    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok(aiMatchmakerService.testAiConnection());
    }

    /**
     * Endpoint for immediate analysis without saving to the database.
     * Useful for quick "What-if" testing with different PDFs and descriptions.
     */
    @PostMapping(value = "/match", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AtsScoreResponse>> calculateMatch(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription) {

        // 1. Extract raw text from the PDF
        String resumeText = pdfExtractionService.extractTextFromPdf(file);

        // 2. Get the structured score from the AI "Brain"
        AtsScoreResponse score = aiMatchmakerService.calculateAtsScore(resumeText, jobDescription);

        return ResponseEntity.ok(ApiResponse.success(score, "AI successfully analyzed the resume."));
    }

    /**
     * Production Endpoint: Analyzes an existing Resume against a Job Listing
     * and saves the result to the user's history.
     */
    @PostMapping("/analyze-and-save")
    public ResponseEntity<ApiResponse<AtsScoreResponse>> analyzeAndSave(
            @RequestParam("resumeId") Long resumeId,
            @RequestParam("jobId") Long jobId) {

        // Temporary fix: Hardcoding user ID to 1 to bypass the SecurityUtils error
        Long currentUserId = 1L;

        // This calls the Service which handles extraction, AI analysis,
        // and database saving in one transaction.
        AtsScoreResponse response = atsService.analyzeResume(
                resumeId,
                jobId,
                currentUserId
        );

        return ResponseEntity.ok(ApiResponse.success(response, "Analysis completed and saved to history."));
    }
}