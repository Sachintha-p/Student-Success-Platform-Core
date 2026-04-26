package com.sliit.studentplatform.module3.service;

import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module3.dto.request.EventRequest;
import com.sliit.studentplatform.module3.dto.response.EventResponse;
import com.sliit.studentplatform.module3.entity.CampusEvent;
import com.sliit.studentplatform.module3.repository.CampusEventRepository;
import com.sliit.studentplatform.module3.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventServiceImpl Unit Tests")
class EventServiceImplTest {

    @Mock
    private CampusEventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private CampusEvent event;
    private EventRequest eventRequest;

    @BeforeEach
    void setUp() {
        event = CampusEvent.builder()
                .id(1L)
                .title("Tech Talk")
                .description("AI Trends")
                .eventDate(LocalDateTime.now().plusDays(1))
                .venue("Hall A")
                .category("Technology")
                .organizerId(101L)
                .maxParticipants(100)
                .isOnline(false)
                .isPublished(true)
                .build();

        eventRequest = EventRequest.builder()
                .title("Tech Talk")
                .description("AI Trends")
                .eventDate(LocalDateTime.now().plusDays(1))
                .venue("Hall A")
                .category("Technology")
                .organizerId(101L)
                .maxAttendees(100)
                .isOnline(false)
                .isPublished(true)
                .build();
    }

    @Test
    @DisplayName("createEvent - should save and return event response")
    void createEvent_shouldSaveAndReturnResponse() {
        when(eventRepository.save(any(CampusEvent.class))).thenReturn(event);

        EventResponse response = eventService.createEvent(eventRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Tech Talk");
        verify(eventRepository, times(1)).save(any(CampusEvent.class));
    }

    @Test
    @DisplayName("getAllEvents - should return list of event responses")
    void getAllEvents_shouldReturnList() {
        when(eventRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.singletonList(event));

        List<EventResponse> responses = eventService.getAllEvents();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo("Tech Talk");
        verify(eventRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("getEventById - should return event response when id exists")
    void getEventById_shouldReturnResponse() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventResponse response = eventService.getEventById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getEventById - should throw ResourceNotFoundException when id does not exist")
    void getEventById_shouldThrowException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateEvent - should update and return event response")
    void updateEvent_shouldUpdateAndReturnResponse() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(CampusEvent.class))).thenReturn(event);

        EventResponse response = eventService.updateEvent(1L, eventRequest);

        assertThat(response).isNotNull();
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(CampusEvent.class));
    }

    @Test
    @DisplayName("deleteEvent - should call deleteById when id exists")
    void deleteEvent_shouldCallDelete() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("getEventsByCategory - should return list")
    void getEventsByCategory_shouldReturnList() {
        when(eventRepository.findByCategory("Technology")).thenReturn(Collections.singletonList(event));

        List<EventResponse> responses = eventService.getEventsByCategory("Technology");

        assertThat(responses).hasSize(1);
        verify(eventRepository, times(1)).findByCategory("Technology");
    }

    @Test
    @DisplayName("getUpcomingEvents - should return list")
    void getUpcomingEvents_shouldReturnList() {
        when(eventRepository.findByEventDateAfter(any(LocalDateTime.class))).thenReturn(Collections.singletonList(event));

        List<EventResponse> responses = eventService.getUpcomingEvents();

        assertThat(responses).hasSize(1);
        verify(eventRepository, times(1)).findByEventDateAfter(any(LocalDateTime.class));
    }
}
