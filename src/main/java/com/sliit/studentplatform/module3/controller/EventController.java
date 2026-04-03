package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.dto.request.EventRequest;
import com.sliit.studentplatform.module3.dto.request.RSVPRequest;
import com.sliit.studentplatform.module3.dto.response.EventResponse;
import com.sliit.studentplatform.module3.dto.response.RSVPResponse;
import com.sliit.studentplatform.module3.service.interfaces.IEventService;
import com.sliit.studentplatform.module3.service.interfaces.IRSVPService;
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
@RequestMapping({"/api/v1/events", "/api/module3/events"})
@RequiredArgsConstructor
@Tag(name = "Event Management")
public class EventController {
    private final IEventService eventService;
    private final IRSVPService rsvpService;
    private final com.sliit.studentplatform.auth.repository.UserRepository userRepository;

    private Long getFallbackUserId(UserPrincipal user) {
        if (user != null) return user.getId();
        // Fallback to the first user in the database if authentication is disabled
        return userRepository.findAll().stream()
                .findFirst()
                .map(com.sliit.studentplatform.auth.entity.User::getId)
                .orElse(1L); // Default to 1L if no user exists at all (DataSeeder should prevent this)
    }

    // --- Event Management ---

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        if (request.getOrganizerId() == null || request.getOrganizerId() <= 0) {
            request.setOrganizerId(getFallbackUserId(user));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(eventService.createEvent(request), "Event created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllEvents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "false") boolean upcoming) {
        List<EventResponse> events = eventService.getFilteredEvents(category, upcoming);
        return ResponseEntity.ok(ApiResponse.success(events, "Events retrieved successfully"));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllEvents() {
        return ResponseEntity.ok(ApiResponse.success(eventService.getAllEvents(), "All events retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(eventService.getEventById(id), "Event retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        if (request.getOrganizerId() == null || request.getOrganizerId() <= 0) {
            request.setOrganizerId(getFallbackUserId(user));
        }
        return ResponseEntity.ok(ApiResponse.success(eventService.updateEvent(id, request), "Event updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully"));
    }

    // --- RSVP System ---

    @PostMapping("/{eventId}/rsvp")
    public ResponseEntity<ApiResponse<RSVPResponse>> rsvpToEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody RSVPRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        Long userId = getFallbackUserId(user); // Fallback for disabled auth
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rsvpService.rsvpToEvent(eventId, userId, request), "RSVP successful"));
    }

    @PutMapping("/{eventId}/rsvp")
    public ResponseEntity<ApiResponse<RSVPResponse>> updateRSVPStatus(
            @PathVariable Long eventId,
            @Valid @RequestBody RSVPRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        Long userId = getFallbackUserId(user); // Fallback for disabled auth
        return ResponseEntity.ok(ApiResponse.success(rsvpService.updateRSVPStatus(eventId, userId, request), "RSVP status updated"));
    }

    @GetMapping("/{eventId}/rsvps")
    public ResponseEntity<ApiResponse<List<RSVPResponse>>> getRSVPsForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(ApiResponse.success(rsvpService.getAllRSVPsForEvent(eventId), "RSVPs retrieved successfully"));
    }

    @GetMapping("/joined")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsJoinedByStudent(@AuthenticationPrincipal UserPrincipal user) {
        Long userId = getFallbackUserId(user); // Fallback for disabled auth
        return ResponseEntity.ok(ApiResponse.success(rsvpService.getEventsJoinedByStudent(userId), "Joined events retrieved successfully"));
    }
}
