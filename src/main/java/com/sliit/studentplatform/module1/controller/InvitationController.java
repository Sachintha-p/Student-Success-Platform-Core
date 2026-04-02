package com.sliit.studentplatform.module1.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module1.dto.response.InvitationResponse;
import com.sliit.studentplatform.module1.service.interfaces.IInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final IInvitationService invitationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getMyPendingInvitations(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<InvitationResponse> invites = invitationService.getMyPendingInvitations(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(invites, "Fetched pending invitations successfully"));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptInvitation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        invitationService.acceptInvitation(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Invitation accepted successfully"));
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<ApiResponse<Void>> declineInvitation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        invitationService.declineInvitation(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Invitation declined successfully"));
    }
}