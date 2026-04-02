package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.module3.entity.Milestone;
import com.sliit.studentplatform.module3.enums.MilestoneStatus;
import com.sliit.studentplatform.module3.repository.MilestoneRepository;
import com.sliit.studentplatform.notification.service.interfaces.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MilestoneReminderService {

  private final MilestoneRepository milestoneRepository;
  private final INotificationService notificationService;

  /**
   * Check for upcoming and overdue milestones every day at 8:00 AM.
   */
  @Scheduled(cron = "0 0 8 * * *")
  public void sendMilestoneReminders() {
    log.info("Starting milestone reminder job...");
    
    LocalDate today = LocalDate.now();
    LocalDate threeDaysFromNow = today.plusDays(3);

    // 1. Remind about milestones due in 3 days
    List<Milestone> upcoming = milestoneRepository.findByStatusNotAndDueDateBetween(
        MilestoneStatus.COMPLETED, today, threeDaysFromNow);
    
    for (Milestone m : upcoming) {
      String title = "Upcoming Milestone: " + m.getTitle();
      String message = "The milestone '" + m.getTitle() + "' is due on " + m.getDueDate() + ". Current progress: " + m.getProgressPercentage() + "%";
      
      // Notify the project creator
      notificationService.sendNotification(
          m.getProject().getCreator().getId(), 
          title, 
          message, 
          "MILESTONE_REMINDER", 
          m.getId(), 
          "Milestone"
      );
    }

    // 2. Remind about overdue milestones
    List<Milestone> overdue = milestoneRepository.findByStatusNotAndDueDateBefore(
        MilestoneStatus.COMPLETED, today);

    for (Milestone m : overdue) {
      String title = "Overdue Milestone: " + m.getTitle();
      String message = "The milestone '" + m.getTitle() + "' was due on " + m.getDueDate() + " and is currently overdue.";
      
      notificationService.sendNotification(
          m.getProject().getCreator().getId(), 
          title, 
          message, 
          "MILESTONE_OVERDUE", 
          m.getId(), 
          "Milestone"
      );
    }
    
    log.info("Milestone reminder job completed.");
  }
}
