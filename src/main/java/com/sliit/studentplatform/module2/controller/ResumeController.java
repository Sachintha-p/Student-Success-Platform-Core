package com.sliit.studentplatform.module2.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module2.dto.response.ResumeResponse;
import com.sliit.studentplatform.module2.entity.AtsAnalysis;
import com.sliit.studentplatform.module2.repository.AtsAnalysisRepository;
import com.sliit.studentplatform.module2.service.interfaces.IResumeService;
import com.sliit.studentplatform.module2.service.interfaces.PdfExtractionService;
import com.sliit.studentplatform.module2.service.interfaces.IAtsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume Management", description = "AI-Powered CV Analysis")
public class ResumeController {

  private final IResumeService resumeService;
  private final PdfExtractionService pdfExtractionService;
  private final IAtsService atsService;
  private final AtsAnalysisRepository atsAnalysisRepository;

  @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Map<String, Object>>> scanAts(
          @RequestParam("file") MultipartFile file,
          @AuthenticationPrincipal UserPrincipal currentUser) {

    try {
      // 1. Save Resume (Populates 'resumes' table)
      ResumeResponse savedResume = resumeService.uploadResume(file, currentUser.getId());

      // 2. Extract Text
      String extractedText = pdfExtractionService.extractTextFromPdf(file);

      // 3. AI Analysis (Now cleans JSON and populates 'ats_analysis' table)
      Map<String, Object> aiResults = atsService.analyzeResume(extractedText, savedResume.getId());

      return ResponseEntity.ok(ApiResponse.success(aiResults, "AI Analysis Completed"));

    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(ApiResponse.error("File processing failed: " + e.getMessage()));
    }
  }

  // CRUD Methods
  @GetMapping
  public ResponseEntity<ApiResponse<List<ResumeResponse>>> myResumes(@AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(resumeService.getMyResumes(currentUser.getId()), "Resumes retrieved"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    resumeService.deleteResume(id, currentUser.getId());
    return ResponseEntity.ok(ApiResponse.success("Resume deleted"));
  }
}