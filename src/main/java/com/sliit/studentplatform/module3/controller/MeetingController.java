package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.dto.request.AvailabilityRequest;
import com.sliit.studentplatform.module3.dto.request.CreateMeetingRequest;
import com.sliit.studentplatform.module3.dto.response.AvailabilitySummaryResponse;
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

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RestController
@RequestMapping({"/api/v1/meetings", "/api/module3/meetings"})
@RequiredArgsConstructor
@Tag(name = "Meetings")
public class  MeetingController {
  private final IMeetingService meetingService;
  private final com.sliit.studentplatform.auth.repository.UserRepository userRepository;

  private Long getUserId(UserPrincipal user) {
    if (user != null) return user.getId();
    return userRepository.findAll().stream()
        .findFirst()
        .map(com.sliit.studentplatform.auth.entity.User::getId)
        .orElse(1L);
  }

  @PostMapping
  public ResponseEntity<ApiResponse<MeetingResponse>> create(
      @Valid @RequestBody CreateMeetingRequest req,
      @AuthenticationPrincipal UserPrincipal user) {
    Long userId = getUserId(user);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(meetingService.createMeeting(req, userId), "Meeting scheduled"));
  }

  @GetMapping("/all")
  public ResponseEntity<ApiResponse<List<MeetingResponse>>> getAllMeetings() {
    return ResponseEntity.ok(ApiResponse.success(meetingService.getAllMeetings(), "All meetings retrieved"));
  }

  @GetMapping("/my")
  public ResponseEntity<ApiResponse<List<MeetingResponse>>> getMyMeetings(
      @AuthenticationPrincipal UserPrincipal user) {
    Long userId = getUserId(user);
    return ResponseEntity.ok(ApiResponse.success(meetingService.getMyMeetings(userId), "My meetings retrieved"));
  }

  @GetMapping("/group/{groupId}")
  public ResponseEntity<ApiResponse<List<MeetingResponse>>> getByGroup(@PathVariable Long groupId) {
    return ResponseEntity.ok(ApiResponse.success(meetingService.getMeetingsForGroup(groupId), "Meetings retrieved"));
  }

  @PostMapping("/{meetingId}/availability")
  public ResponseEntity<ApiResponse<Void>> submitAvailability(@PathVariable Long meetingId,
      @Valid @RequestBody AvailabilityRequest req,
      @AuthenticationPrincipal UserPrincipal user) {
    Long userId = getUserId(user);
    meetingService.submitAvailability(meetingId, req, userId);
    return ResponseEntity.ok(ApiResponse.success(null, "Availability submitted"));
  }

  @GetMapping("/{meetingId}/summary")
  public ResponseEntity<ApiResponse<List<AvailabilitySummaryResponse>>> getSummary(@PathVariable Long meetingId) {
    return ResponseEntity.ok(ApiResponse.success(meetingService.getAvailabilitySummary(meetingId), "Summary retrieved"));
  }

  @PutMapping("/{meetingId}/finalize")
  public ResponseEntity<ApiResponse<MeetingResponse>> finalizeMeeting(@PathVariable Long meetingId,
      @AuthenticationPrincipal UserPrincipal user) {
    Long userId = getUserId(user);
    return ResponseEntity.ok(ApiResponse.success(meetingService.finalizeMeetingTime(meetingId, userId), "Meeting finalized"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<MeetingResponse>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(meetingService.getMeetingDetails(id), "Meeting details retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<MeetingResponse>> update(@PathVariable Long id,
      @Valid @RequestBody CreateMeetingRequest req,
      @AuthenticationPrincipal UserPrincipal user) {
    Long userId = getUserId(user);
    return ResponseEntity.ok(ApiResponse.success(meetingService.updateMeeting(id, req, userId), "Meeting updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
      @AuthenticationPrincipal UserPrincipal user) {
    Long userId = getUserId(user);
    meetingService.deleteMeeting(id, userId);
    return ResponseEntity.ok(ApiResponse.success(null, "Meeting deleted"));
  }
}
