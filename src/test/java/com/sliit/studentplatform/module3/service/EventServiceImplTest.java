package com.sliit.studentplatform.module3.service;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ConflictException;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module3.dto.request.CreateEventRequest;
import com.sliit.studentplatform.module3.dto.response.EventResponse;
import com.sliit.studentplatform.module3.entity.CampusEvent;
import com.sliit.studentplatform.module3.entity.EventRsvp;
import com.sliit.studentplatform.module3.repository.CampusEventRepository;
import com.sliit.studentplatform.module3.repository.EventRsvpRepository;
import com.sliit.studentplatform.module3.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EventServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventServiceImpl Unit Tests")
class EventServiceImplTest {

  @Mock
  private CampusEventRepository eventRepository;
  @Mock
  private EventRsvpRepository rsvpRepository;
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private EventServiceImpl eventService;

  private User organizer;
  private CampusEvent event;

  @BeforeEach
  void setUp() {
    organizer = User.builder().id(1L).fullName("Carol White").email("carol@sliit.lk").build();

    event = CampusEvent.builder()
        .id(5L).title("Spring Boot Workshop").organizer(organizer)
        .eventDate(LocalDateTime.now().plusDays(7)).maxAttendees(50)
        .published(true).build();
  }

  @Test
  @DisplayName("createEvent — should persist event with published=false by default")
  void createEvent_shouldPersistEventUnpublished() {
    CreateEventRequest req = CreateEventRequest.builder()
        .title("Spring Boot Workshop").eventDate(LocalDateTime.now().plusDays(10))
        .venue("Lab 301").maxAttendees(50).build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(organizer));
    when(eventRepository.save(any(CampusEvent.class))).thenReturn(event);
    when(rsvpRepository.countByEventId(5L)).thenReturn(0);

    EventResponse response = eventService.createEvent(req, 1L);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(5L);
    assertThat(response.getTitle()).isEqualTo("Spring Boot Workshop");
    verify(eventRepository, times(1)).save(any(CampusEvent.class));
  }

  @Test
  @DisplayName("rsvpToEvent — should add RSVP when not already registered")
  void rsvpToEvent_shouldAddRsvpSuccessfully() {
    when(eventRepository.findById(5L)).thenReturn(Optional.of(event));
    when(rsvpRepository.existsByEventIdAndUserId(5L, 2L)).thenReturn(false);
    User user = User.builder().id(2L).fullName("David Lee").build();
    when(userRepository.findById(2L)).thenReturn(Optional.of(user));
    when(rsvpRepository.save(any(EventRsvp.class))).thenReturn(new EventRsvp());
    when(rsvpRepository.countByEventId(5L)).thenReturn(1);

    EventResponse response = eventService.rsvpToEvent(5L, 2L);

    assertThat(response.getRsvpCount()).isEqualTo(1);
    verify(rsvpRepository, times(1)).save(any(EventRsvp.class));
  }

  @Test
  @DisplayName("rsvpToEvent — should throw ConflictException when already RSVPd")
  void rsvpToEvent_shouldThrowConflictWhenAlreadyRsvpd() {
    when(eventRepository.findById(5L)).thenReturn(Optional.of(event));
    when(rsvpRepository.existsByEventIdAndUserId(5L, 1L)).thenReturn(true);

    assertThatThrownBy(() -> eventService.rsvpToEvent(5L, 1L))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("Already RSVPd");
  }

  @Test
  @DisplayName("listPublishedEvents — should return paged events")
  void listPublishedEvents_shouldReturnPagedResult() {
    when(eventRepository.findByPublishedTrue(any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.singletonList(event)));
    when(rsvpRepository.countByEventId(5L)).thenReturn(3);

    PagedResponse<EventResponse> response = eventService.listPublishedEvents(PageRequest.of(0, 10));

    assertThat(response.getContent()).hasSize(1);
    assertThat(response.getContent().get(0).getRsvpCount()).isEqualTo(3);
  }
}
