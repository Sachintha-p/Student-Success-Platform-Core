package com.sliit.studentplatform.notification.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.notification.entity.Notification;
import com.sliit.studentplatform.notification.service.interfaces.INotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Retrieve and manage user notifications")
public class NotificationController {

  private final INotificationService notificationService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<Notification>>> myNotifications(
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(ApiResponse.success(
        notificationService.getNotificationsForUser(user.getId()), "Notifications retrieved"));
  }

  @GetMapping("/unread")
  public ResponseEntity<ApiResponse<List<Notification>>> unread(@AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(ApiResponse.success(
        notificationService.getUnreadNotifications(user.getId()), "Unread retrieved"));
  }

  @GetMapping("/unread/count")
  public ResponseEntity<ApiResponse<Long>> unreadCount(@AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(ApiResponse.success(
        notificationService.countUnread(user.getId()), "Unread count retrieved"));
  }

  @PatchMapping("/{id}/read")
  public ResponseEntity<ApiResponse<Void>> markRead(
      @PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
    notificationService.markAsRead(id, user.getId());
    return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
  }

  @PatchMapping("/read-all")
  public ResponseEntity<ApiResponse<Void>> markAllRead(@AuthenticationPrincipal UserPrincipal user) {
    notificationService.markAllAsRead(user.getId());
    return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
  }
}
