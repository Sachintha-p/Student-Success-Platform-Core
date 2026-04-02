package com.sliit.studentplatform.admin.controller;

import com.sliit.studentplatform.admin.service.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("systemAdminController") // <-- FIX: Custom bean name prevents conflicts
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Change to your React port
public class AdminController {

    private final ScraperService scraperService;

    @PostMapping("/scrape-jobs")
    public ResponseEntity<String> triggerScrape() {
        String result = scraperService.runJobScraper();
        return ResponseEntity.ok(result);
    }
}