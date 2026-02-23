package com.sliit.studentplatform.module4.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/** An AI-recommended study resource (lecture notes, links, videos). */
@Entity
@Table(name = "study_resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyResource extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Column(nullable = false, length = 100)
  private String subject;
  @Column(length = 50)
  private String type; // VIDEO | ARTICLE | PDF | COURSE
  @Column(nullable = false)
  private String url;
  @Column(name = "tags", columnDefinition = "text[]")
  private String[] tags;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "added_by")
  private User addedBy;
}
