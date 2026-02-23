package com.sliit.studentplatform.module4.repository;

import com.sliit.studentplatform.module4.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
  List<Conversation> findByUserIdAndActiveTrue(Long userId);
}
