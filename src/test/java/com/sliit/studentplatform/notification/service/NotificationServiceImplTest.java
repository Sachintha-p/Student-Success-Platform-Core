package com.sliit.studentplatform.notification.service;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.notification.entity.Notification;
import com.sliit.studentplatform.notification.repository.NotificationRepository;
import com.sliit.studentplatform.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link NotificationServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationServiceImpl Unit Tests")
class NotificationServiceImplTest {

  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private NotificationServiceImpl notificationService;

  private User recipient;

  @BeforeEach
  void setUp() {
    recipient = User.builder().id(2L).fullName("Frank Brown").email("frank@sliit.lk").build();
  }

  @Test
  @DisplayName("sendNotification — should persist notification with correct fields")
  void sendNotification_shouldPersistNotification() {
    // Arrange
    when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));
    Notification saved = Notification.builder()
        .id(1L).recipient(recipient).title("Team Invite")
        .message("You have a new invitation").type("INVITATION")
        .referenceId(10L).referenceType("TeamInvitation").read(false).build();
    when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

    // Act
    Notification result = notificationService.sendNotification(
        2L, "Team Invite", "You have a new invitation", "INVITATION", 10L, "TeamInvitation");

    // Assert
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getTitle()).isEqualTo("Team Invite");
    assertThat(result.isRead()).isFalse();
    verify(notificationRepository, times(1)).save(any(Notification.class));
  }

  @Test
  @DisplayName("sendNotification — should throw when recipient not found")
  void sendNotification_shouldThrowWhenRecipientNotFound() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> notificationService.sendNotification(999L, "Test", "message", "SYSTEM", null, null))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("markAllAsRead — should set all unread notifications to read")
  void markAllAsRead_shouldSetAllToRead() {
    // Arrange
    Notification n1 = Notification.builder().id(1L).recipient(recipient).title("N1").type("SYSTEM").read(false).build();
    Notification n2 = Notification.builder().id(2L).recipient(recipient).title("N2").type("TASK").read(false).build();
    when(notificationRepository.findByRecipientIdAndReadFalse(2L)).thenReturn(List.of(n1, n2));
    when(notificationRepository.saveAll(anyList())).thenReturn(List.of(n1, n2));

    // Act
    notificationService.markAllAsRead(2L);

    // Assert
    assertThat(n1.isRead()).isTrue();
    assertThat(n2.isRead()).isTrue();
    verify(notificationRepository, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("countUnread — should return correct unread count")
  void countUnread_shouldReturnCorrectCount() {
    when(notificationRepository.countByRecipientIdAndReadFalse(2L)).thenReturn(5L);

    long count = notificationService.countUnread(2L);

    assertThat(count).isEqualTo(5L);
  }

  @Test
  @DisplayName("getNotificationsForUser — should return ordered list")
  void getNotificationsForUser_shouldReturnList() {
    Notification n = Notification.builder().id(1L).recipient(recipient).title("N1").type("INVITATION").build();
    when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(2L)).thenReturn(List.of(n));

    var notifications = notificationService.getNotificationsForUser(2L);

    assertThat(notifications).hasSize(1);
    assertThat(notifications.get(0).getTitle()).isEqualTo("N1");
  }
}
