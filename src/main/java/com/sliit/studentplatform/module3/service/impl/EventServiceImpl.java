package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ConflictException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module3.dto.request.CreateEventRequest;
import com.sliit.studentplatform.module3.dto.response.EventResponse;
import com.sliit.studentplatform.module3.entity.CampusEvent;
import com.sliit.studentplatform.module3.entity.EventRsvp;
import com.sliit.studentplatform.module3.repository.CampusEventRepository;
import com.sliit.studentplatform.module3.repository.EventRsvpRepository;
import com.sliit.studentplatform.module3.service.interfaces.IEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements IEventService {

  private final CampusEventRepository eventRepository;
  private final EventRsvpRepository rsvpRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public EventResponse createEvent(CreateEventRequest request, Long organizerId) {
    log.info("Creating event '{}' by user: {}", request.getTitle(), organizerId);
    User organizer = userRepository.findById(organizerId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", organizerId));
    CampusEvent event = CampusEvent.builder()
        .title(request.getTitle()).description(request.getDescription())
        .eventDate(request.getEventDate()).venue(request.getVenue())
        .online(request.isOnline()).maxAttendees(request.getMaxAttendees())
        .organizer(organizer).published(false).build();
    return mapToResponse(eventRepository.save(event), 0);
  }

  @Override
  @Transactional(readOnly = true)
  public EventResponse getEventById(Long eventId) {
    CampusEvent event = getOrThrow(eventId);
    return mapToResponse(event, rsvpRepository.countByEventId(eventId));
  }

  @Override
  @Transactional(readOnly = true)
  public PagedResponse<EventResponse> listPublishedEvents(Pageable pageable) {
    return PagedResponse.of(eventRepository.findByPublishedTrue(pageable)
        .map(e -> mapToResponse(e, rsvpRepository.countByEventId(e.getId()))));
  }

  @Override
  @Transactional
  public EventResponse updateEvent(Long eventId, CreateEventRequest request, Long userId) {
    CampusEvent event = getOrThrow(eventId);
    assertOrganizer(event, userId);
    event.setTitle(request.getTitle());
    event.setDescription(request.getDescription());
    event.setEventDate(request.getEventDate());
    event.setVenue(request.getVenue());
    event.setOnline(request.isOnline());
    event.setMaxAttendees(request.getMaxAttendees());
    return mapToResponse(eventRepository.save(event), rsvpRepository.countByEventId(eventId));
  }

  @Override
  @Transactional
  public void deleteEvent(Long eventId, Long userId) {
    assertOrganizer(getOrThrow(eventId), userId);
    eventRepository.deleteById(eventId);
  }

  @Override
  @Transactional
  public EventResponse rsvpToEvent(Long eventId, Long userId) {
    CampusEvent event = getOrThrow(eventId);
    if (rsvpRepository.existsByEventIdAndUserId(eventId, userId))
      throw new ConflictException("Already RSVPd to this event");
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    rsvpRepository.save(EventRsvp.builder().event(event).user(user).build());
    return mapToResponse(event, rsvpRepository.countByEventId(eventId));
  }

  @Override
  @Transactional
  public EventResponse cancelRsvp(Long eventId, Long userId) {
    rsvpRepository.findAll().stream()
        .filter(r -> r.getEvent().getId().equals(eventId) && r.getUser().getId().equals(userId))
        .findFirst().ifPresent(rsvpRepository::delete);
    return getEventById(eventId);
  }

  private CampusEvent getOrThrow(Long id) {
    return eventRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("CampusEvent", "id", id));
  }

  private void assertOrganizer(CampusEvent e, Long userId) {
    if (!e.getOrganizer().getId().equals(userId))
      throw new UnauthorizedException("Only the organizer can modify this event");
  }

  private EventResponse mapToResponse(CampusEvent e, int rsvpCount) {
    return EventResponse.builder()
        .id(e.getId()).title(e.getTitle()).description(e.getDescription())
        .eventDate(e.getEventDate()).venue(e.getVenue()).online(e.isOnline())
        .maxAttendees(e.getMaxAttendees()).rsvpCount(rsvpCount)
        .organizerId(e.getOrganizer().getId()).organizerName(e.getOrganizer().getFullName())
        .published(e.isPublished()).createdAt(e.getCreatedAt()).build();
  }
}
