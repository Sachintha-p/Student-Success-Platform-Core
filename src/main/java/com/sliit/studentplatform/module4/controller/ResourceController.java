package com.sliit.studentplatform.module4.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module4.dto.response.ResourceResponse;
import com.sliit.studentplatform.module4.service.interfaces.IResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
@Tag(name = "Study Resources")
public class ResourceController {
  private final IResourceService resourceService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<ResourceResponse>>> search(
      @RequestParam(required = false) String subject,
      @RequestParam(required = false) String type,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity
        .ok(ApiResponse.success(resourceService.searchResources(subject, type, user.getId()), "Resources retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ResourceResponse>> getById(@PathVariable Long id,
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity
        .ok(ApiResponse.success(resourceService.getResourceById(id, user.getId()), "Resource retrieved"));
  }

  @GetMapping("/recommendations")
  public ResponseEntity<ApiResponse<List<ResourceResponse>>> recommendations(
      @RequestParam String topic, @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(
        ApiResponse.success(resourceService.getAiRecommendations(topic, user.getId()), "Recommendations generated"));
  }
}
