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

import java.util.List;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume Management", description = "Upload and manage student CVs")
public class ResumeController {

  private final IResumeService resumeService;
  private final PdfExtractionService pdfExtractionService; // <-- Added your new extraction service

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ResumeResponse>> upload(
          @RequestParam("file") MultipartFile file,
          @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(resumeService.uploadResume(file, currentUser.getId()), "Resume uploaded"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ResumeResponse>>> myResumes(
          @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(resumeService.getMyResumes(currentUser.getId()), "Resumes retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ResumeResponse>> getResume(
          @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity
            .ok(ApiResponse.success(resumeService.getResumeById(id, currentUser.getId()), "Resume retrieved"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(
          @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    resumeService.deleteResume(id, currentUser.getId());
    return ResponseEntity.ok(ApiResponse.success("Resume deleted"));
  }

  @PatchMapping("/{id}/primary")
  public ResponseEntity<ApiResponse<ResumeResponse>> setPrimary(
          @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity
            .ok(ApiResponse.success(resumeService.setPrimaryResume(id, currentUser.getId()), "Primary resume updated"));
  }

  // =========================================================================================
  // NEW TEST ENDPOINT: POST /api/v1/resumes/extract
  // =========================================================================================
  @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<String>> testPdfExtraction(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body(ApiResponse.error("Please upload a valid PDF file."));
    }

    String extractedText = pdfExtractionService.extractTextFromPdf(file);

    return ResponseEntity.ok(ApiResponse.success(extractedText, "PDF text extracted successfully"));
  }
}