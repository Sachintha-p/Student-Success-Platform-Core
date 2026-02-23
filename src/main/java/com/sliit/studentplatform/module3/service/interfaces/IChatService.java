package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module3.entity.GroupChatMessage;
import org.springframework.data.domain.Pageable;

public interface IChatService {
  GroupChatMessage sendMessage(Long groupId, Long senderId, String content);

  PagedResponse<GroupChatMessage> getMessages(Long groupId, Pageable pageable);

  void deleteMessage(Long messageId, Long userId);
}
