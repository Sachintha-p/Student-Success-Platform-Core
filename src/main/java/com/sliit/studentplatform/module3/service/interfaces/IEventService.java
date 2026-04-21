package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.module3.dto.request.EventRequest;
import com.sliit.studentplatform.module3.dto.response.EventResponse;

import java.util.List;

public interface IEventService {
    EventResponse createEvent(EventRequest request);

    List<EventResponse> getAllEvents();

    EventResponse getEventById(Long id);

    EventResponse updateEvent(Long id, EventRequest request);

    void deleteEvent(Long id);

    List<EventResponse> getEventsByCategory(String category);

    List<EventResponse> getUpcomingEvents();

    List<EventResponse> getFilteredEvents(String category, boolean upcoming);
}
