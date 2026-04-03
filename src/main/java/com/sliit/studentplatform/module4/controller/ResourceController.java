package com.sliit.studentplatform.module4.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.common.security.UserPrincipal;
import com.sliit.studentplatform.module4.dto.response.ResourceResponse;
import com.sliit.studentplatform.module4.service.interfaces.IResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    Long userId = (user != null) ? user.getId() : null;
    return ResponseEntity
        .ok(ApiResponse.success(resourceService.searchResources(subject, type, userId), "Resources retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ResourceResponse>> getById(@PathVariable Long id,
      @AuthenticationPrincipal UserPrincipal user) {
    Long userId = (user != null) ? user.getId() : null;
    return ResponseEntity
        .ok(ApiResponse.success(resourceService.getResourceById(id, userId), "Resource retrieved"));
  }

  @GetMapping("/recommendations")
  public ResponseEntity<ApiResponse<List<ResourceResponse>>> recommendations(
      @RequestParam String topic, @AuthenticationPrincipal UserPrincipal user) {
    Long userId = (user != null) ? user.getId() : null;
    return ResponseEntity.ok(
        ApiResponse.success(resourceService.getAiRecommendations(topic, userId), "Recommendations generated"));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ResourceResponse>> create(@Valid @RequestBody com.sliit.studentplatform.module4.dto.request.ResourceRequest request) {
    return ResponseEntity.ok(ApiResponse.success(resourceService.createResource(request), "Resource created"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ResourceResponse>> update(@PathVariable Long id, @Valid @RequestBody com.sliit.studentplatform.module4.dto.request.ResourceRequest request) {
    return ResponseEntity.ok(ApiResponse.success(resourceService.updateResource(id, request), "Resource updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
    resourceService.deleteResource(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Resource deleted"));
  }
}
