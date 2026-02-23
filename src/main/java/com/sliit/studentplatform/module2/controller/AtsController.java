package com.sliit.studentplatform.module2.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module2.dto.response.AtsScoreResponse;
import com.sliit.studentplatform.module2.service.interfaces.IAtsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ats")
@RequiredArgsConstructor
@Tag(name = "ATS Analysis", description = "AI-powered CV ATS scoring against job listings")
public class AtsController {

  private final IAtsService atsService;

  @PostMapping("/analyze")
  public ResponseEntity<ApiResponse<AtsScoreResponse>> analyze(
      @RequestParam Long resumeId,
      @RequestParam Long jobListingId,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(
        atsService.analyzeResume(resumeId, jobListingId, currentUser.getId()), "ATS analysis complete"));
  }

  @GetMapping("/history")
  public ResponseEntity<ApiResponse<List<AtsScoreResponse>>> history(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity
        .ok(ApiResponse.success(atsService.getAnalysisHistory(currentUser.getId()), "Analysis history"));
  }
}
