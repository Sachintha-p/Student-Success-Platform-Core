package com.sliit.studentplatform.module3.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** RSVP record for a campus event. */
@Entity
@Table(name = "event_rsvps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRsvp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private CampusEvent event;

  @Column(name = "user_id", nullable = false)
  private Long studentId;

  @Column(nullable = false, length = 20)
  private String status;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime rsvpDate;
}
