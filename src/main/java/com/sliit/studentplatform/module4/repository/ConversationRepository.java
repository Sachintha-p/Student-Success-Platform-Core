package com.sliit.studentplatform.module4.repository;

import com.sliit.studentplatform.module4.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
  List<Conversation> findByUserIdAndActiveTrue(Long userId);

  @org.springframework.data.jpa.repository.Query("SELECT c.subject, COUNT(c.id) FROM Conversation c GROUP BY c.subject ORDER BY COUNT(c.id) DESC")
  List<Object[]> countConversationsBySubject();

  List<Conversation> findTop10ByActiveTrueOrderByCreatedAtDesc();
}
