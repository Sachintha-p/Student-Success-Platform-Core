package com.sliit.studentplatform.module4.service.interfaces;

import com.sliit.studentplatform.module4.dto.request.AiQueryRequest;
import com.sliit.studentplatform.module4.dto.response.AiQueryResponse;

public interface IAiAssistantService {
  AiQueryResponse askQuestion(AiQueryRequest request, Long userId);
}
