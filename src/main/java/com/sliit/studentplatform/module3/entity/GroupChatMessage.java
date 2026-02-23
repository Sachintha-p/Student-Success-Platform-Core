package com.sliit.studentplatform.module3.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import jakarta.persistence.*;
import lombok.*;

/** A chat message in a project group's chat room. */
@Entity
@Table(name = "group_chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChatMessage extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private ProjectGroup group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;
  @Column(name = "message_type", length = 20)
  @Builder.Default
  private String messageType = "TEXT";
  @Column(name = "file_url")
  private String fileUrl;
  @Column(name = "is_deleted")
  @Builder.Default
  private boolean deleted = false;
}
