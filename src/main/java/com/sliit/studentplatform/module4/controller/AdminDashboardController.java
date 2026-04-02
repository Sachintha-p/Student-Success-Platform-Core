package com.sliit.studentplatform.module4.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import com.sliit.studentplatform.module4.dto.response.DashboardStatsResponse;
import com.sliit.studentplatform.module4.service.interfaces.IAdminDashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/module4/admin")
@RequiredArgsConstructor
@Tag(name = "Module 4 Admin Dashboard")
public class AdminDashboardController {

    private final IAdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(adminDashboardService.getDashboardStats(), "Admin stats retrieved"));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/conversations/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteConversation(@org.springframework.web.bind.annotation.PathVariable Long id) {
        // Soft delete logic already exists in AiAssistantController, 
        // but for TRUE admin moderation we might want to deactivate or really delete.
        // Let's use the repository to deactivate it or delete it.
        // For project purposes, we will perform a real delete from the repository 
        // if the admin specifically requests it via this moderation panel.
        adminDashboardService.deleteConversation(id);
        return ResponseEntity.ok(ApiResponse.success("Success", "Conversation deleted by admin"));
    }
}
