package com.sliit.studentplatform.module3.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** A campus event organised by students or staff. */
@Entity
@Table(name = "campus_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampusEvent extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Column(name = "event_date", nullable = false)
  private LocalDateTime eventDate;
  @Column(length = 200)
  private String venue;
  @Column(name = "is_online")
  @Builder.Default
  private boolean online = false;
  @Column(name = "max_attendees")
  private Integer maxAttendees;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organizer_id", nullable = false)
  private User organizer;

  @Column(name = "is_published")
  @Builder.Default
  private boolean published = false;
}
