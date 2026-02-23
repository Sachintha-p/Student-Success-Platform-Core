package com.sliit.studentplatform.module1.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module1.dto.response.MatchScoreResponse;
import com.sliit.studentplatform.module1.service.interfaces.IMatchingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matching")
@RequiredArgsConstructor
@Tag(name = "Team Matching", description = "AI-powered skill-based matching for groups and students")
public class MatchingController {

  private final IMatchingService matchingService;

  @GetMapping("/my-groups")
  public ResponseEntity<ApiResponse<List<MatchScoreResponse>>> compatibleGroupsForMe(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(
        matchingService.findCompatibleGroupsForStudent(currentUser.getId()),
        "Compatible groups for you"));
  }

  @GetMapping("/group/{groupId}/students")
  public ResponseEntity<ApiResponse<List<MatchScoreResponse>>> compatibleStudentsForGroup(
      @PathVariable Long groupId) {
    return ResponseEntity.ok(ApiResponse.success(
        matchingService.findCompatibleStudentsForGroup(groupId),
        "Compatible students for group"));
  }

  @GetMapping("/score")
  public ResponseEntity<ApiResponse<MatchScoreResponse>> matchScore(
      @RequestParam Long groupId,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(
        matchingService.calculateMatchScore(currentUser.getId(), groupId),
        "Match score calculated"));
  }
}
