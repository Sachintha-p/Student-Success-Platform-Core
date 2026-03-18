package com.sliit.studentplatformbackend.module3.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "event_rsvps",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"event_id", "student_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRsvp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_id", nullable = false)
  private Long eventId;

  @Column(name = "student_id", nullable = false)
  private Long studentId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private RsvpStatus status;
}