package com.sliit.studentplatform.module4.entity;

import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/** A single message in an AI conversation. */
@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  @JsonIgnore
  private Conversation conversation;

  /** ROLE: USER | ASSISTANT */
  @Column(nullable = false, length = 20)
  private String role;
  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;
  @Column(name = "token_count")
  private Integer tokenCount;
}
