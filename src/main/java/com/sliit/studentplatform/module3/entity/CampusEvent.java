package com.sliit.studentplatform.module3.entity;

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

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "event_date", nullable = false)
  private LocalDateTime eventDate;

  @Column(name = "venue")
  private String venue;

  @Column
  private String category;

  @Column(name = "organizer_id", nullable = false)
  private Long organizerId;

  @Column(name = "max_attendees")
  private Integer maxParticipants;

  @Column(name = "is_published", nullable = false)
  private Boolean isPublished;

  @Column(name = "is_online", nullable = false)
  private Boolean isOnline;
}
