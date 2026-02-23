package com.sliit.studentplatform.module2.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.common.enums.Priority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/** A job or internship listing posted by a recruiter or lecturer. */
@Entity
@Table(name = "job_listings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobListing extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false, length = 200)
  private String company;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "required_skills", columnDefinition = "text[]")
  private String[] requiredSkills;

  @Column(length = 50)
  private String type; // FULL_TIME | PART_TIME | INTERNSHIP

  @Column(length = 150)
  private String location;

  @Column(name = "is_remote")
  private boolean remote;

  @Column(name = "deadline")
  private LocalDate deadline;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  @Builder.Default
  private Priority priority = Priority.MEDIUM;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "posted_by", nullable = false)
  private User postedBy;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private boolean active = true;
}
