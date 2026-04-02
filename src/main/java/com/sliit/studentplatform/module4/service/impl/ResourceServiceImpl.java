package com.sliit.studentplatform.module4.service.impl;

import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module4.dto.response.ResourceResponse;
import com.sliit.studentplatform.module4.entity.StudyResource;
import com.sliit.studentplatform.module4.repository.BookmarkRepository;
import com.sliit.studentplatform.module4.repository.StudyResourceRepository;
import com.sliit.studentplatform.module4.service.interfaces.IResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements IResourceService {

  private final StudyResourceRepository resourceRepository;
  private final BookmarkRepository bookmarkRepository;
  private final ChatClient chatClient;

  @Override
  @Transactional(readOnly = true)
  public List<ResourceResponse> searchResources(String subject, String type, Long userId) {
    List<StudyResource> resources;
    if (subject != null && type != null) {
      resources = resourceRepository.findBySubjectIgnoreCase(subject).stream()
          .filter(r -> r.getType() != null && r.getType().equalsIgnoreCase(type))
          .collect(Collectors.toList());
    } else if (subject != null) {
      resources = resourceRepository.findBySubjectIgnoreCase(subject);
    } else if (type != null) {
      resources = resourceRepository.findByType(type);
    } else {
      resources = resourceRepository.findAll();
    }
    return resources.stream().map(r -> mapToResponse(r, userId)).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public ResourceResponse getResourceById(Long id, Long userId) {
    return mapToResponse(resourceRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("StudyResource", "id", id)), userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ResourceResponse> getAiRecommendations(String topic, Long userId) {
    log.info("Getting AI resource recommendations for topic: {}", topic);
    
    List<StudyResource> resources = resourceRepository.findBySubjectContainingIgnoreCase(topic);
    if (resources.isEmpty()) {
      resources = resourceRepository.findByTitleContainingIgnoreCase(topic);
    }
    if (resources.isEmpty()) {
      resources = resourceRepository.findByTag(topic);
    }
    return resources.stream()
        .limit(3)
        .map(r -> mapToResponse(r, userId))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ResourceResponse createResource(com.sliit.studentplatform.module4.dto.request.ResourceRequest request) {
    StudyResource resource = StudyResource.builder()
        .title(request.getTitle())
        .url(request.getUrl())
        .description(request.getDescription())
        .subject(request.getSubject())
        .type(request.getType())
        .tags(request.getTags() != null ? request.getTags().toArray(new String[0]) : new String[0])
        .build();
    return mapToResponse(resourceRepository.save(resource), null);
  }

  @Override
  @Transactional
  public ResourceResponse updateResource(Long id, com.sliit.studentplatform.module4.dto.request.ResourceRequest request) {
    StudyResource resource = resourceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Resource not found"));
    resource.setTitle(request.getTitle());
    resource.setUrl(request.getUrl());
    resource.setDescription(request.getDescription());
    resource.setSubject(request.getSubject());
    resource.setType(request.getType());
    resource.setTags(request.getTags() != null ? request.getTags().toArray(new String[0]) : new String[0]);
    return mapToResponse(resourceRepository.save(resource), null);
  }

  @Override
  @Transactional
  public void deleteResource(Long id) {
    if (!resourceRepository.existsById(id)) {
      throw new ResourceNotFoundException("StudyResource", "id", id);
    }
    bookmarkRepository.deleteByResourceId(id);
    resourceRepository.deleteById(id);
  }

  private ResourceResponse mapToResponse(StudyResource r, Long userId) {
    boolean bookmarked = userId != null && bookmarkRepository.existsByUserIdAndResourceId(userId, r.getId());
    return ResourceResponse.builder().id(r.getId()).title(r.getTitle())
        .description(r.getDescription()).subject(r.getSubject())
        .type(r.getType()).url(r.getUrl()).tags(r.getTags()).bookmarked(bookmarked).build();
  }
}
