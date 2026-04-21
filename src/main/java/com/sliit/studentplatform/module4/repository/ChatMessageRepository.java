package com.sliit.studentplatform.module4.repository;

import com.sliit.studentplatform.module4.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // ✅ THIS LINE

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  Page<ChatMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId, Pageable pageable);

  List<ChatMessage> findByConversationId(Long conversationId); // ✅ NEW

  void deleteByConversationId(Long conversationId);
}