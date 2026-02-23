package com.sliit.studentplatform.module4.service.interfaces;

import com.sliit.studentplatform.module4.dto.response.ResourceResponse;
import java.util.List;

public interface IResourceService {
  List<ResourceResponse> searchResources(String subject, String type, Long userId);

  ResourceResponse getResourceById(Long id, Long userId);

  List<ResourceResponse> getAiRecommendations(String topic, Long userId);
}
