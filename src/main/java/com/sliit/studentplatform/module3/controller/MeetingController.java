package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.dto.request.CreateMeetingRequest;
import com.sliit.studentplatform.module3.dto.response.MeetingResponse;
import com.sliit.studentplatform.module3.service.interfaces.IMeetingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
@Tag(name = "Meetings")
public class  MeetingController {
  private final IMeetingService meetingService;

  @PostMapping
  public ResponseEntity<ApiResponse<MeetingResponse>> create(@Valid @RequestBody CreateMeetingRequest req,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(meetingService.createMeeting(req, user.getId()), "Meeting scheduled"));
  }

  @GetMapping("/group/{groupId}")
  public ResponseEntity<ApiResponse<List<MeetingResponse>>> getByGroup(@PathVariable Long groupId) {
    return ResponseEntity.ok(ApiResponse.success(meetingService.getMeetingsForGroup(groupId), "Meetings retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<MeetingResponse>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(meetingService.getMeeting(id), "Meeting retrieved"));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponse<MeetingResponse>> updateStatus(@PathVariable Long id,
      @RequestParam String status, @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity
        .ok(ApiResponse.success(meetingService.updateMeetingStatus(id, status, user.getId()), "Status updated"));
  }
}
