package com.sliit.studentplatform.module3.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Availability window submitted by a group member for meeting scheduling. */
@Entity
@Table(name = "meeting_availability", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "meeting_id", "user_id" }, name = "uq_meeting_availability")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingAvailability extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "meeting_id", nullable = false)
  private Meeting meeting;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ElementCollection
  @CollectionTable(name = "meeting_available_dates", joinColumns = @JoinColumn(name = "availability_id"))
  @Column(name = "available_date")
  @Builder.Default
  private List<LocalDateTime> availableDates = new ArrayList<>();

  @Column(name = "response_date")
  @Builder.Default
  private LocalDateTime responseDate = LocalDateTime.now();
}
