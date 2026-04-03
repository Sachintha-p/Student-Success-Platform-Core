package com.sliit.studentplatform.module4.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/** A bookmarked study resource for a user. */
@Entity
@Table(name = "bookmarks", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "user_id", "resource_id" }, name = "uq_bookmark")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resource_id", nullable = false)
  @JsonIgnore
  private StudyResource resource;

  @Column(columnDefinition = "TEXT")
  private String note;
}
