package com.sliit.studentplatform.module4.service.interfaces;

import com.sliit.studentplatform.module4.dto.response.ResourceResponse;
import java.util.List;

public interface IBookmarkService {
  ResourceResponse addBookmark(Long resourceId, String note, Long userId);

  void removeBookmark(Long resourceId, Long userId);

  List<ResourceResponse> getMyBookmarks(Long userId);
}
