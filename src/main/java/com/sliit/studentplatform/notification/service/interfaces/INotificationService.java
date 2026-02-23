package com.sliit.studentplatform.notification.service.interfaces;

import com.sliit.studentplatform.notification.entity.Notification;
import java.util.List;

/**
 * Service contract for notification delivery (Interface Segregation Principle).
 *
 * <p>
 * Concrete implementations: {@code DatabaseNotificationServiceImpl} (stored in
 * DB),
 * {@code WebSocketNotificationServiceImpl} (real-time push). They are
 * interchangeable
 * (Liskov Substitution) and the default is determined by {@code @Primary}.
 */
public interface INotificationService {

  Notification sendNotification(Long recipientId, String title, String message,
      String type, Long referenceId, String referenceType);

  List<Notification> getNotificationsForUser(Long userId);

  List<Notification> getUnreadNotifications(Long userId);

  void markAsRead(Long notificationId, Long userId);

  void markAllAsRead(Long userId);

  long countUnread(Long userId);
}
