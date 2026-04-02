package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module3.dto.request.EventRequest;
import com.sliit.studentplatform.module3.dto.response.EventResponse;
import com.sliit.studentplatform.module3.entity.CampusEvent;
import com.sliit.studentplatform.module3.repository.CampusEventRepository;
import com.sliit.studentplatform.module3.service.interfaces.IEventService;
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
public class EventServiceImpl implements IEventService {

    private final CampusEventRepository eventRepository;

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request) {
        log.info("Creating event with title: {}, organizerId: {}", request.getTitle(), request.getOrganizerId());
        try {
            CampusEvent event = CampusEvent.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .eventDate(request.getEventDate())
                    .venue(request.getVenue())
                    .category(request.getCategory())
                    .organizerId(request.getOrganizerId())
                    .maxParticipants(request.getMaxAttendees() != null ? request.getMaxAttendees() : 0)
                    .isOnline(Boolean.TRUE.equals(request.getIsOnline()))
                    .isPublished(Boolean.TRUE.equals(request.getIsPublished()))
                    .build();
            CampusEvent savedEvent = eventRepository.save(event);
            log.info("Event saved successfully with id: {}", savedEvent.getId());
            return mapToResponse(savedEvent);
        } catch (Exception e) {
            log.error("Error creating event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        return eventRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        log.info("Updating event id: {}, organizerId: {}", id, request.getOrganizerId());
        CampusEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        try {
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setEventDate(request.getEventDate());
            event.setVenue(request.getVenue());
            event.setCategory(request.getCategory());
            event.setOrganizerId(request.getOrganizerId());
            event.setMaxParticipants(request.getMaxAttendees() != null ? request.getMaxAttendees() : 0);
            if (request.getIsOnline() != null) event.setIsOnline(request.getIsOnline());
            if (request.getIsPublished() != null) event.setIsPublished(request.getIsPublished());

            CampusEvent savedEvent = eventRepository.save(event);
            log.info("Event updated successfully with id: {}", savedEvent.getId());
            return mapToResponse(savedEvent);
        } catch (Exception e) {
            log.error("Error updating event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event", "id", id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByCategory(String category) {
        return eventRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents() {
        return eventRepository.findByEventDateAfter(LocalDateTime.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getFilteredEvents(String category, boolean upcoming) {
        LocalDateTime now = LocalDateTime.now();
        List<CampusEvent> events;
        
        if (category != null && upcoming) {
            events = eventRepository.findByCategoryAndEventDateAfter(category, now);
        } else if (category != null) {
            events = eventRepository.findByCategory(category);
        } else if (upcoming) {
            events = eventRepository.findByEventDateAfter(now);
        } else {
            events = eventRepository.findAll();
        }
        
        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private EventResponse mapToResponse(CampusEvent event) {
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
                .createdBy(event.getCreatedBy() != null ? event.getCreatedBy() : "SYSTEM")
                .createdAt(event.getCreatedAt())
                .build();
    }
}
