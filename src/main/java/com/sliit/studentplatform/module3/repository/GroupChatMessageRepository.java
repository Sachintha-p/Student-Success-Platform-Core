package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.GroupChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatMessageRepository extends JpaRepository<GroupChatMessage, Long> {
  Page<GroupChatMessage> findByGroupIdAndDeletedFalseOrderByCreatedAtAsc(Long groupId, Pageable pageable);
}
