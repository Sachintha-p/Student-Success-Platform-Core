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
      @RequestParam(required = false) String note, 
      @RequestParam(required = false) Long userId,
      @AuthenticationPrincipal UserPrincipal user) {
    Long finalUserId = (user != null) ? user.getId() : (userId != null ? userId : 12L);
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponse.success(bookmarkService.addBookmark(resourceId, note, finalUserId), "Bookmarked"));
  }

  @DeleteMapping("/{resourceId}")
  public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long resourceId,
      @RequestParam(required = false) Long userId,
      @AuthenticationPrincipal UserPrincipal user) {
    Long finalUserId = (user != null) ? user.getId() : (userId != null ? userId : 12L);
    bookmarkService.removeBookmark(resourceId, finalUserId);
    return ResponseEntity.ok(ApiResponse.success("Bookmark removed"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ResourceResponse>>> myBookmarks(
      @RequestParam(required = false) Long userId,
      @AuthenticationPrincipal UserPrincipal user) {
    Long finalUserId = (user != null) ? user.getId() : (userId != null ? userId : 12L);
    return ResponseEntity.ok(ApiResponse.success(bookmarkService.getMyBookmarks(finalUserId), "Bookmarks retrieved"));
  }
}
