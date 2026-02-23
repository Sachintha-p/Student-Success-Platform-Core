package com.sliit.studentplatform.module4.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ConflictException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module4.dto.response.ResourceResponse;
import com.sliit.studentplatform.module4.entity.Bookmark;
import com.sliit.studentplatform.module4.entity.StudyResource;
import com.sliit.studentplatform.module4.repository.BookmarkRepository;
import com.sliit.studentplatform.module4.repository.StudyResourceRepository;
import com.sliit.studentplatform.module4.service.interfaces.IBookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkServiceImpl implements IBookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final StudyResourceRepository resourceRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ResourceResponse addBookmark(Long resourceId, String note, Long userId) {
    if (bookmarkRepository.existsByUserIdAndResourceId(userId, resourceId))
      throw new ConflictException("Resource is already bookmarked");
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    StudyResource resource = resourceRepository.findById(resourceId)
        .orElseThrow(() -> new ResourceNotFoundException("StudyResource", "id", resourceId));
    bookmarkRepository.save(Bookmark.builder().user(user).resource(resource).note(note).build());
    return mapToResourceResponse(resource, true);
  }

  @Override
  @Transactional
  public void removeBookmark(Long resourceId, Long userId) {
    bookmarkRepository.findByUserId(userId).stream()
        .filter(b -> b.getResource().getId().equals(resourceId))
        .findFirst().ifPresent(bookmarkRepository::delete);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ResourceResponse> getMyBookmarks(Long userId) {
    return bookmarkRepository.findByUserId(userId).stream()
        .map(b -> mapToResourceResponse(b.getResource(), true)).collect(Collectors.toList());
  }

  private ResourceResponse mapToResourceResponse(StudyResource r, boolean bookmarked) {
    return ResourceResponse.builder().id(r.getId()).title(r.getTitle())
        .description(r.getDescription()).subject(r.getSubject())
        .type(r.getType()).url(r.getUrl()).tags(r.getTags()).bookmarked(bookmarked).build();
  }
}
