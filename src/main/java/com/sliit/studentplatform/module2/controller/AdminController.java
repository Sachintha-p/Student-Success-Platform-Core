package com.sliit.studentplatform.module2.controller;

import com.sliit.studentplatform.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testAdminAccess() {
        return ResponseEntity.ok(ApiResponse.success("SUCCESS! You are an Admin.", "Access Granted"));
    }
}