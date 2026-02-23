package com.sliit.studentplatform.module4.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module4.dto.response.ResourceResponse;
import com.sliit.studentplatform.module4.service.interfaces.IBookmarkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
@Tag(name = "Bookmarks")
public class BookmarkController {
  private final IBookmarkService bookmarkService;

  @PostMapping("/{resourceId}")
  public ResponseEntity<ApiResponse<ResourceResponse>> add(@PathVariable Long resourceId,
      @RequestParam(required = false) String note, @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponse.success(bookmarkService.addBookmark(resourceId, note, user.getId()), "Bookmarked"));
  }

  @DeleteMapping("/{resourceId}")
  public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long resourceId,
      @AuthenticationPrincipal UserPrincipal user) {
    bookmarkService.removeBookmark(resourceId, user.getId());
    return ResponseEntity.ok(ApiResponse.success("Bookmark removed"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ResourceResponse>>> myBookmarks(@AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(ApiResponse.success(bookmarkService.getMyBookmarks(user.getId()), "Bookmarks retrieved"));
  }
}
