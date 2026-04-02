package com.sliit.studentplatform.module1.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module1.dto.request.CreateGroupRequest;
import com.sliit.studentplatform.module1.dto.request.TeamEmailInviteRequest;
import com.sliit.studentplatform.module1.dto.response.GroupResponse;
import com.sliit.studentplatform.module1.dto.response.JoinRequestResponse;
import com.sliit.studentplatform.module1.service.interfaces.ITeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Tag(name = "Team Management", description = "Create and manage project groups")
public class TeamController {

    private final ITeamService teamService;

    @Operation(summary = "Create a new project group")
    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        GroupResponse response = teamService.createGroup(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Group created successfully"));
    }

    @Operation(summary = "Get a project group by ID")
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(ApiResponse.success(teamService.getGroupById(groupId), "Group retrieved"));
    }

    @Operation(summary = "List all open groups (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<GroupResponse>>> listOpenGroups(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(teamService.listOpenGroups(PageRequest.of(page, size)), "Groups retrieved"));
    }

    @Operation(summary = "Get groups the current user has joined")
    @GetMapping("/my-teams")
    public ResponseEntity<ApiResponse<PagedResponse<GroupResponse>>> getMyGroups(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(teamService.getMyGroups(currentUser.getId(), PageRequest.of(page, size)), "My groups retrieved"));
    }

    @Operation(summary = "Update a project group")
    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable Long groupId, @Valid @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(teamService.updateGroup(groupId, request, currentUser.getId()), "Group updated successfully"));
    }

    @Operation(summary = "Delete a project group")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable Long groupId, @AuthenticationPrincipal UserPrincipal currentUser) {
        teamService.deleteGroup(groupId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully"));
    }

    @Operation(summary = "Join an open project group")
    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse<Void>> joinGroup(
            @PathVariable Long groupId, @AuthenticationPrincipal UserPrincipal currentUser) {
        teamService.joinGroup(groupId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Join Request Sent Successfully!"));
    }

    @Operation(summary = "Leave a project group")
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveGroup(
            @PathVariable Long groupId, @AuthenticationPrincipal UserPrincipal currentUser) {
        teamService.leaveGroup(groupId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Left group successfully"));
    }

    @Operation(summary = "Search groups by keyword")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<GroupResponse>>> searchGroups(
            @RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(teamService.searchGroups(keyword, PageRequest.of(page, size)), "Search results"));
    }

    @Operation(summary = "Invite a student to the team via email")
    @PostMapping("/{groupId}/invite")
    public ResponseEntity<ApiResponse<Void>> inviteUser(
            @PathVariable Long groupId, @Valid @RequestBody TeamEmailInviteRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        teamService.inviteUserByEmail(groupId, request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Invitation sent successfully"));
    }

    // =====================================================================================
    // ================== NEW FIX: ENDPOINTS FOR FETCHING JOIN REQUESTS ====================
    // =====================================================================================

    @Operation(summary = "Get pending join requests for teams I own")
    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse<List<JoinRequestResponse>>> getPendingRequests(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(teamService.getPendingJoinRequests(currentUser.getId()), "Fetched pending requests"));
    }

    @Operation(summary = "Accept a student's join request")
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptJoinRequest(
            @PathVariable Long requestId, @AuthenticationPrincipal UserPrincipal currentUser) {
        teamService.acceptJoinRequest(requestId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Join request accepted successfully"));
    }

    @Operation(summary = "Decline a student's join request")
    @PostMapping("/requests/{requestId}/decline")
    public ResponseEntity<ApiResponse<Void>> declineJoinRequest(
            @PathVariable Long requestId, @AuthenticationPrincipal UserPrincipal currentUser) {
        teamService.declineJoinRequest(requestId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Join request declined successfully"));
    }
}