package com.sliit.studentplatform.module2.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module2.dto.response.ResumeResponse;
import com.sliit.studentplatform.module2.service.interfaces.IResumeService;
import com.sliit.studentplatform.module2.service.interfaces.PdfExtractionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume Management", description = "Upload and manage student CVs")
public class ResumeController {

  private final IResumeService resumeService;
  private final PdfExtractionService pdfExtractionService;

  // 1. STANDARD UPLOAD (Saves the file)
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ResumeResponse>> upload(
          @RequestParam("file") MultipartFile file,
          @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(resumeService.uploadResume(file, currentUser.getId()), "Resume uploaded"));
  }

  // 2. GET ALL MY RESUMES
  @GetMapping
  public ResponseEntity<ApiResponse<List<ResumeResponse>>> myResumes(
          @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(resumeService.getMyResumes(currentUser.getId()), "Resumes retrieved"));
  }

  // 3. GET SINGLE RESUME
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ResumeResponse>> getResume(
          @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity
            .ok(ApiResponse.success(resumeService.getResumeById(id, currentUser.getId()), "Resume retrieved"));
  }

  // 4. DELETE RESUME
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(
          @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    resumeService.deleteResume(id, currentUser.getId());
    return ResponseEntity.ok(ApiResponse.success("Resume deleted"));
  }

  // 5. SET PRIMARY RESUME
  @PatchMapping("/{id}/primary")
  public ResponseEntity<ApiResponse<ResumeResponse>> setPrimary(
          @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity
            .ok(ApiResponse.success(resumeService.setPrimaryResume(id, currentUser.getId()), "Primary resume updated"));
  }

  // 6. RAW EXTRACTION TEST
  @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<String>> testPdfExtraction(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body(ApiResponse.error("Please upload a valid PDF file."));
    }
    String extractedText = pdfExtractionService.extractTextFromPdf(file);
    return ResponseEntity.ok(ApiResponse.success(extractedText, "PDF text extracted successfully"));
  }

  // =========================================================================================
  // 7. NEW ATS SCANNER ENDPOINT: POST /api/v1/resumes/scan
  // =========================================================================================
  @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Map<String, Object>>> scanAts(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body(ApiResponse.error("Please upload a valid PDF file."));
    }

    // A. Extract the text
    String extractedText = pdfExtractionService.extractTextFromPdf(file).toLowerCase();

    // B. Define expected keywords
    List<String> expectedKeywords = List.of(
            "java", "spring boot", "react", "docker", "aws", "mysql", "agile", "github", "javascript", "rest api"
    );

    List<String> matched = new ArrayList<>();
    List<String> missing = new ArrayList<>();

    // C. Check which keywords are in the PDF text
    for (String word : expectedKeywords) {
      if (extractedText.contains(word.toLowerCase())) {
        matched.add(word);
      } else {
        missing.add(word);
      }
    }

    // D. Calculate score out of 100
    int score = 0;
    if (!expectedKeywords.isEmpty()) {
      score = (int) (((double) matched.size() / expectedKeywords.size()) * 100);
    }

    // E. Build the response payload
    Map<String, Object> atsResult = new HashMap<>();
    atsResult.put("atsScore", score);
    atsResult.put("keywordMatch", matched.size() + "/" + expectedKeywords.size());
    atsResult.put("matchedKeywords", matched);
    atsResult.put("missingKeywords", missing);

    return ResponseEntity.ok(ApiResponse.success(atsResult, "ATS Scan Completed"));
  }
}