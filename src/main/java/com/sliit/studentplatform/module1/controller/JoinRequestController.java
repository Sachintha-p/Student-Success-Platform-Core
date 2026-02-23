package com.sliit.studentplatform.module1.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module1.dto.request.JoinRequestDto;
import com.sliit.studentplatform.module1.dto.response.InvitationResponse;
import com.sliit.studentplatform.module1.service.interfaces.IJoinRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/join-requests")
@RequiredArgsConstructor
@Tag(name = "Join Requests", description = "Submit and manage group join requests")
public class JoinRequestController {

  private final IJoinRequestService joinRequestService;

  @PostMapping
  public ResponseEntity<ApiResponse<InvitationResponse>> submit(
      @Valid @RequestBody JoinRequestDto request,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponse.success(joinRequestService.submitJoinRequest(request, currentUser.getId()),
            "Join request submitted"));
  }

  @PatchMapping("/{id}/approve")
  public ResponseEntity<ApiResponse<InvitationResponse>> approve(
      @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(
        joinRequestService.approveJoinRequest(id, currentUser.getId()), "Join request approved"));
  }

  @PatchMapping("/{id}/reject")
  public ResponseEntity<ApiResponse<InvitationResponse>> reject(
      @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(
        joinRequestService.rejectJoinRequest(id, currentUser.getId()), "Join request rejected"));
  }

  @GetMapping("/my")
  public ResponseEntity<ApiResponse<List<InvitationResponse>>> myRequests(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(
        joinRequestService.getMyJoinRequests(currentUser.getId()), "Join requests retrieved"));
  }

  @GetMapping("/group/{groupId}")
  public ResponseEntity<ApiResponse<List<InvitationResponse>>> groupRequests(
      @PathVariable Long groupId, @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(
        joinRequestService.getPendingRequestsForGroup(groupId, currentUser.getId()), "Join requests retrieved"));
  }
}
