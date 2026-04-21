package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.common.exception.ConflictException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.ValidationException;
import com.sliit.studentplatform.module3.dto.request.RSVPRequest;
import com.sliit.studentplatform.module3.dto.response.EventResponse;
import com.sliit.studentplatform.module3.dto.response.RSVPResponse;
import com.sliit.studentplatform.module3.entity.CampusEvent;
import com.sliit.studentplatform.module3.entity.EventRsvp;
import com.sliit.studentplatform.module3.repository.CampusEventRepository;
import com.sliit.studentplatform.module3.repository.EventRsvpRepository;
import com.sliit.studentplatform.module3.service.interfaces.IRSVPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RSVPServiceImpl implements IRSVPService {

    private final EventRsvpRepository rsvpRepository;
    private final CampusEventRepository eventRepository;

    @Override
    @Transactional
    public RSVPResponse rsvpToEvent(Long eventId, Long studentId, RSVPRequest request) {
        log.info("Student {} RSVPing to event {} with status {}", studentId, eventId, request.getStatus());
        
        CampusEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (rsvpRepository.existsByEventIdAndStudentId(eventId, studentId)) {
            throw new ConflictException("You have already RSVP'd to this event");
        }

        // Limit participants check
        if ("GOING".equals(request.getStatus())) {
            int goingCount = rsvpRepository.countByEventIdAndStatus(eventId, "GOING");
            if (event.getMaxParticipants() != null && goingCount >= event.getMaxParticipants()) {
                throw new ValidationException("Event is already full");
            }
        }

        EventRsvp rsvp = EventRsvp.builder()
                .event(event)
                .studentId(studentId)
                .status(request.getStatus())
                .rsvpDate(LocalDateTime.now())
                .build();

        return mapToResponse(rsvpRepository.save(rsvp));
    }

    @Override
    @Transactional
    public RSVPResponse updateRSVPStatus(Long eventId, Long studentId, RSVPRequest request) {
        EventRsvp rsvp = rsvpRepository.findByEventIdAndStudentId(eventId, studentId);
        if (rsvp == null) {
            throw new ResourceNotFoundException("RSVP", "eventId/studentId", eventId + "/" + studentId);
        }

        // Limit participants check if status is changing to GOING
        if ("GOING".equals(request.getStatus()) && !"GOING".equals(rsvp.getStatus())) {
            int goingCount = rsvpRepository.countByEventIdAndStatus(eventId, "GOING");
            if (rsvp.getEvent().getMaxParticipants() != null && goingCount >= rsvp.getEvent().getMaxParticipants()) {
                throw new ValidationException("Event is already full");
            }
        }

        rsvp.setStatus(request.getStatus());
        rsvp.setRsvpDate(LocalDateTime.now());

        return mapToResponse(rsvpRepository.save(rsvp));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RSVPResponse> getAllRSVPsForEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event", "id", eventId);
        }
        return rsvpRepository.findByEventId(eventId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsJoinedByStudent(Long studentId) {
        return rsvpRepository.findByStudentId(studentId).stream()
                .map(rsvp -> mapEventToResponse(rsvp.getEvent()))
                .collect(Collectors.toList());
    }

    private RSVPResponse mapToResponse(EventRsvp rsvp) {
        return RSVPResponse.builder()
                .id(rsvp.getId())
                .eventId(rsvp.getEvent().getId())
                .studentId(rsvp.getStudentId())
                .status(rsvp.getStatus())
                .rsvpDate(rsvp.getRsvpDate())
                .build();
    }

    private EventResponse mapEventToResponse(CampusEvent event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .venue(event.getVenue())
                .category(event.getCategory())
                .organizerId(event.getOrganizerId())
                .maxAttendees(event.getMaxParticipants())
                .isOnline(event.getIsOnline())
                .isPublished(event.getIsPublished())
                .createdBy(event.getCreatedBy())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
