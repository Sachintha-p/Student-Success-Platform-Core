package com.sliit.studentplatform.module2.controller;

import com.sliit.studentplatform.module2.service.interfaces.PdfExtractionService;
import com.sliit.studentplatform.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/ats")
public class AtsController {

  private final PdfExtractionService pdfExtractionService;

  // Constructor Injection (Recommended)
  public AtsController(PdfExtractionService pdfExtractionService) {
    this.pdfExtractionService = pdfExtractionService;
  }

  @PostMapping("/match-cv")
  public ResponseEntity<ApiResponse<String>> processCvUpload(
          @RequestParam("cv") MultipartFile cvFile) {

    try {
      // Changed from extractTextFromPDF to extractTextFromPdf to match your Service
      String extractedText = pdfExtractionService.extractTextFromPdf(cvFile);

      // Verify extraction in console
      System.out.println("Extracted CV Text:\n" + extractedText);

      return ResponseEntity.ok(ApiResponse.success(extractedText, "PDF Parsed Successfully"));

    } catch (IOException e) {
      // Log the error and return a failure response
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Failed to parse the PDF document: " + e.getMessage()));
    }
  }
}