package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module3.dto.request.CreateEventRequest;
import com.sliit.studentplatform.module3.dto.response.EventResponse;
import org.springframework.data.domain.Pageable;

public interface IEventService {
  EventResponse createEvent(CreateEventRequest request, Long organizerId);

  EventResponse getEventById(Long eventId);

  PagedResponse<EventResponse> listPublishedEvents(Pageable pageable);

  EventResponse updateEvent(Long eventId, CreateEventRequest request, Long userId);

  void deleteEvent(Long eventId, Long userId);

  EventResponse rsvpToEvent(Long eventId, Long userId);

  EventResponse cancelRsvp(Long eventId, Long userId);
}
