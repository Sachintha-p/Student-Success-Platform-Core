package com.sliit.studentplatform.module3.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** A scheduled meeting for a project group. */
@Entity
@Table(name = "group_meetings")
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

  @ElementCollection
  @CollectionTable(name = "meeting_proposed_dates", joinColumns = @JoinColumn(name = "meeting_id"))
  @Column(name = "proposed_date")
  @Builder.Default
  private List<LocalDateTime> proposedDates = new ArrayList<>();

  @Column(name = "final_date")
  private LocalDateTime finalDate;

  private String location;

  @Column(name = "meeting_link")
  private String meetingLink;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", nullable = false)
  private User creator;
}
