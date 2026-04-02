package com.sliit.studentplatform.module1.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module1.dto.GroupRequest;
import com.sliit.studentplatform.module1.dto.GroupResponse;
import com.sliit.studentplatform.module1.service.impl.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(
            @Valid @RequestBody GroupRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        GroupResponse response = groupService.createGroup(request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Project group created successfully"));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        GroupResponse response = groupService.updateGroup(groupId, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Project group updated successfully"));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        groupService.deleteGroup(groupId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Project group deleted successfully"));
    }
}