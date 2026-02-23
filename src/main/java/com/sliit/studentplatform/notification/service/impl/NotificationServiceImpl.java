package com.sliit.studentplatform.notification.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.notification.entity.Notification;
import com.sliit.studentplatform.notification.repository.NotificationRepository;
import com.sliit.studentplatform.notification.service.interfaces.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Database-backed {@link INotificationService} implementation.
 *
 * <p>
 * Marked {@code @Primary} so it is injected by default wherever
 * {@code INotificationService} is required (Dependency Inversion Principle).
 * A WebSocket push implementation can replace or extend this without modifying
 * callers (Open/Closed Principle).
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements INotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public Notification sendNotification(Long recipientId, String title, String message,
      String type, Long referenceId, String referenceType) {
    log.info("Sending notification to user {}: {}", recipientId, title);
    User recipient = userRepository.findById(recipientId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", recipientId));

    return notificationRepository.save(Notification.builder()
        .recipient(recipient)
        .title(title)
        .message(message)
        .type(type)
        .referenceId(referenceId)
        .referenceType(referenceType)
        .read(false)
        .build());
  }

  @Override
  @Transactional(readOnly = true)
  public List<Notification> getNotificationsForUser(Long userId) {
    return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Notification> getUnreadNotifications(Long userId) {
    return notificationRepository.findByRecipientIdAndReadFalse(userId);
  }

  @Override
  @Transactional
  public void markAsRead(Long notificationId, Long userId) {
    notificationRepository.findById(notificationId).ifPresent(n -> {
      n.setRead(true);
      notificationRepository.save(n);
    });
  }

  @Override
  @Transactional
  public void markAllAsRead(Long userId) {
    List<Notification> unread = notificationRepository.findByRecipientIdAndReadFalse(userId);
    unread.forEach(n -> n.setRead(true));
    notificationRepository.saveAll(unread);
  }

  @Override
  @Transactional(readOnly = true)
  public long countUnread(Long userId) {
    return notificationRepository.countByRecipientIdAndReadFalse(userId);
  }
}
