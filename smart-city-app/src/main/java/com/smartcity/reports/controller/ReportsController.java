package com.smartcity.reports.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, String>>> categories() {
        return ResponseEntity.ok(List.of(
                Map.of("code", "graffiti", "label", "Graffiti"),
                Map.of("code", "iluminat", "label", "Iluminat public"),
                Map.of("code", "infrastructura", "label", "Infrastructura"),
                Map.of("code", "curatenie", "label", "Curatenie")));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submit(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(Map.of(
                "status", "received",
                "category", request.getOrDefault("category", "general"),
                "message", "Sesizarea a fost inregistrata in backbone-ul initial."));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Map<String, Object>>> mine(Authentication authentication) {
        return ResponseEntity.ok(List.of(
                Map.of("id", 1, "category", "infrastructura", "status", "NEW", "submittedBy", authentication.getName()),
                Map.of("id", 2, "category", "iluminat", "status", "IN_PROGRESS", "submittedBy", authentication.getName())));
    }
}
