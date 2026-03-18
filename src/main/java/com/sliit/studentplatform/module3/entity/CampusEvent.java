package com.sliit.studentplatformbackend.module3.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "campus_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampusEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String title;

  @Column(nullable = false, length = 1000)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private EventCategory category;

  @Column(nullable = false, length = 150)
  private String location;

  @Column(name = "event_date", nullable = false)
  private LocalDate eventDate;

  @Column(name = "event_time", nullable = false)
  private LocalTime eventTime;

  @Column(nullable = false)
  private Integer capacity;

  @Column(name = "organizer_name", nullable = false, length = 100)
  private String organizerName;
}