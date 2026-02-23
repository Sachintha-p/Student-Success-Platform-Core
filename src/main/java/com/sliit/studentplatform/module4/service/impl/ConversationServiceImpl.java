package com.sliit.studentplatform.module4.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module4.dto.request.CreateConversationRequest;
import com.sliit.studentplatform.module4.dto.response.ConversationResponse;
import com.sliit.studentplatform.module4.entity.Conversation;
import com.sliit.studentplatform.module4.repository.ConversationRepository;
import com.sliit.studentplatform.module4.service.interfaces.IConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements IConversationService {

  private final ConversationRepository conversationRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ConversationResponse createConversation(CreateConversationRequest request, Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    Conversation conv = conversationRepository.save(Conversation.builder()
        .user(user).title(request.getTitle()).subject(request.getSubject()).build());
    return mapToResponse(conv);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ConversationResponse> getUserConversations(Long userId) {
    return conversationRepository.findByUserIdAndActiveTrue(userId).stream()
        .map(this::mapToResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public ConversationResponse getConversation(Long id, Long userId) {
    Conversation conv = conversationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));
    return mapToResponse(conv);
  }

  @Override
  @Transactional
  public void deleteConversation(Long id, Long userId) {
    conversationRepository.findById(id).ifPresent(c -> {
      c.setActive(false);
      conversationRepository.save(c);
    });
  }

  private ConversationResponse mapToResponse(Conversation c) {
    return ConversationResponse.builder().id(c.getId()).userId(c.getUser().getId())
        .title(c.getTitle()).subject(c.getSubject()).active(c.isActive())
        .createdAt(c.getCreatedAt()).build();
  }
}
