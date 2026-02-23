package com.sliit.studentplatform.module1.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module1.dto.request.SendInvitationRequest;
import com.sliit.studentplatform.module1.dto.response.InvitationResponse;
import com.sliit.studentplatform.module1.service.interfaces.IInvitationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
@Tag(name = "Team Invitations", description = "Send, accept, and reject team invitations")
public class InvitationController {

  private final IInvitationService invitationService;

  @PostMapping
  public ResponseEntity<ApiResponse<InvitationResponse>> send(
      @Valid @RequestBody SendInvitationRequest request,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponse.success(invitationService.sendInvitation(request, currentUser.getId()), "Invitation sent"));
  }

  @PatchMapping("/{id}/accept")
  public ResponseEntity<ApiResponse<InvitationResponse>> accept(
      @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(
        invitationService.acceptInvitation(id, currentUser.getId()), "Invitation accepted"));
  }

  @PatchMapping("/{id}/reject")
  public ResponseEntity<ApiResponse<InvitationResponse>> reject(
      @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(
        invitationService.rejectInvitation(id, currentUser.getId()), "Invitation rejected"));
  }

  @GetMapping("/my")
  public ResponseEntity<ApiResponse<List<InvitationResponse>>> myPendingInvitations(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(ApiResponse.success(
        invitationService.getPendingInvitationsForUser(currentUser.getId()), "Invitations retrieved"));
  }
}
