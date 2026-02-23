package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.dto.request.CreateEventRequest;
import com.sliit.studentplatform.module3.dto.response.EventResponse;
import com.sliit.studentplatform.module3.service.interfaces.IEventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Campus Events")
public class EventController {
  private final IEventService eventService;

  @PostMapping
  public ResponseEntity<ApiResponse<EventResponse>> create(@Valid @RequestBody CreateEventRequest req,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(eventService.createEvent(req, user.getId()), "Event created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<EventResponse>>> listEvents(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity
        .ok(ApiResponse.success(eventService.listPublishedEvents(PageRequest.of(page, size)), "Events retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<EventResponse>> getEvent(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(eventService.getEventById(id), "Event retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<EventResponse>> update(@PathVariable Long id,
      @Valid @RequestBody CreateEventRequest req, @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(ApiResponse.success(eventService.updateEvent(id, req, user.getId()), "Event updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
    eventService.deleteEvent(id, user.getId());
    return ResponseEntity.ok(ApiResponse.success("Event deleted"));
  }

  @PostMapping("/{id}/rsvp")
  public ResponseEntity<ApiResponse<EventResponse>> rsvp(@PathVariable Long id,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(ApiResponse.success(eventService.rsvpToEvent(id, user.getId()), "RSVP recorded"));
  }

  @DeleteMapping("/{id}/rsvp")
  public ResponseEntity<ApiResponse<EventResponse>> cancelRsvp(@PathVariable Long id,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(ApiResponse.success(eventService.cancelRsvp(id, user.getId()), "RSVP cancelled"));
  }
}
