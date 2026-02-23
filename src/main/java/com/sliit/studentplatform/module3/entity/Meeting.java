package com.sliit.studentplatform.module3.entity;

import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/** A scheduled meeting for a project group. */
@Entity
@Table(name = "meetings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private ProjectGroup group;

  @Column(nullable = false, length = 200)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String agenda;
  @Column(name = "meeting_time", nullable = false)
  private LocalDateTime meetingTime;
  @Column(name = "duration_minutes")
  private Integer durationMinutes;
  @Column(name = "meeting_link")
  private String meetingLink;
  @Column(nullable = false, length = 20)
  @Builder.Default
  private String status = "SCHEDULED";
}
