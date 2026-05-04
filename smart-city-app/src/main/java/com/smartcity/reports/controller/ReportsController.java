package com.smartcity.reports.controller;

import com.smartcity.common.api.ApiResponse;
import java.util.List;
import java.util.Map;
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
    public ApiResponse<List<Map<String, String>>> categories() {
        return ApiResponse.success(List.of(
                Map.of("code", "graffiti", "label", "Graffiti"),
                Map.of("code", "iluminat", "label", "Iluminat public"),
                Map.of("code", "infrastructura", "label", "Infrastructura"),
                Map.of("code", "curatenie", "label", "Curatenie")),
                "Categorii disponibile pentru raportare.");
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> submit(@RequestBody Map<String, Object> request) {
        return ApiResponse.success(Map.of(
                "status", "received",
                "category", request.getOrDefault("category", "general"),
                "message", "Sesizarea a fost inregistrata in backbone-ul initial."),
                "Raport trimis.");
    }

    @GetMapping("/mine")
    public ApiResponse<List<Map<String, Object>>> mine(Authentication authentication) {
        return ApiResponse.success(List.of(
                Map.of("id", 1, "category", "infrastructura", "status", "NEW", "submittedBy", authentication.getName()),
                Map.of("id", 2, "category", "iluminat", "status", "IN_PROGRESS", "submittedBy", authentication.getName())),
                "Sesizarile utilizatorului curent.");
    }
}
