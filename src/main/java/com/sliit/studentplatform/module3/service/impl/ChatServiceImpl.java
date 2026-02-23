package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module3.entity.GroupChatMessage;
import com.sliit.studentplatform.module3.repository.GroupChatMessageRepository;
import com.sliit.studentplatform.module3.service.interfaces.IChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements IChatService {

  private final GroupChatMessageRepository chatRepository;
  private final ProjectGroupRepository groupRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public GroupChatMessage sendMessage(Long groupId, Long senderId, String content) {
    var group = groupRepository.findById(groupId)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", groupId));
    var sender = userRepository.findById(senderId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));
    return chatRepository.save(GroupChatMessage.builder()
        .group(group).sender(sender).content(content).messageType("TEXT").build());
  }

  @Override
  @Transactional(readOnly = true)
  public PagedResponse<GroupChatMessage> getMessages(Long groupId, Pageable pageable) {
    return PagedResponse.of(chatRepository.findByGroupIdAndDeletedFalseOrderByCreatedAtAsc(groupId, pageable));
  }

  @Override
  @Transactional
  public void deleteMessage(Long messageId, Long userId) {
    chatRepository.findById(messageId).ifPresent(msg -> {
      msg.setDeleted(true);
      chatRepository.save(msg);
    });
  }
}
