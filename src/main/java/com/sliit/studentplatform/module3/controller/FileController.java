package com.sliit.studentplatform.module3.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module3.entity.SharedFile;
import com.sliit.studentplatform.module3.service.interfaces.IFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Shared Files")
public class FileController {
  private final IFileService fileService;

  @PostMapping("/group/{groupId}")
  public ResponseEntity<ApiResponse<SharedFile>> share(
      @PathVariable Long groupId,
      @RequestParam String fileName, @RequestParam String fileUrl,
      @RequestParam(required = false) Long fileSize,
      @RequestParam(required = false) String contentType,
      @RequestParam(required = false) String description,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponse.success(
            fileService.shareFile(groupId, user.getId(), fileName, fileUrl, fileSize, contentType, description),
            "File shared"));
  }

  @GetMapping("/group/{groupId}")
  public ResponseEntity<ApiResponse<List<SharedFile>>> getGroupFiles(@PathVariable Long groupId) {
    return ResponseEntity.ok(ApiResponse.success(fileService.getFilesForGroup(groupId), "Files retrieved"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
    fileService.deleteFile(id, user.getId());
    return ResponseEntity.ok(ApiResponse.success("File deleted"));
  }
}
