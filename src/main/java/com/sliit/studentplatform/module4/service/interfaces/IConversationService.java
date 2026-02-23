package com.sliit.studentplatform.module4.service.interfaces;

import com.sliit.studentplatform.module4.dto.request.CreateConversationRequest;
import com.sliit.studentplatform.module4.dto.response.ConversationResponse;
import java.util.List;

public interface IConversationService {
  ConversationResponse createConversation(CreateConversationRequest request, Long userId);

  List<ConversationResponse> getUserConversations(Long userId);

  ConversationResponse getConversation(Long id, Long userId);

  void deleteConversation(Long id, Long userId);
}
