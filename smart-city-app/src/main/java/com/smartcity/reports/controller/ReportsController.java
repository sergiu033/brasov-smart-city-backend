package com.smartcity.reports.controller;

import com.smartcity.common.api.ApiResponse;
import java.util.List;
import java.util.Map;

import com.smartcity.reports.dto.CityReportRequest;
import com.smartcity.reports.dto.CityReportResponse;
import com.smartcity.reports.service.CityReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final CityReportService cityReportService;

    public ReportsController(CityReportService cityReportService) {
        this.cityReportService = cityReportService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<Map<String, String>>> categories() {
        return ApiResponse.success(List.of(
                Map.of("code", "graffiti", "label", "Graffiti"),
                Map.of("code", "iluminat", "label", "Iluminat public"),
                Map.of("code", "infrastructura", "label", "Infrastructura"),
                Map.of("code", "curatenie", "label", "Curatenie")),
                "Categorii disponibile pentru raportare.");
    }

//    @PostMapping
//    public ApiResponse<Map<String, Object>> submit(@RequestBody Map<String, Object> request) {
//        return ApiResponse.success(Map.of(
//                "status", "received",
//                "category", request.getOrDefault("category", "general"),
//                "message", "Sesizarea a fost inregistrata in backbone-ul initial."),
//                "Raport trimis.");
//    }

    @GetMapping("/mine")
    public ApiResponse<List<Map<String, Object>>> mine(Authentication authentication) {
        return ApiResponse.success(List.of(
                Map.of("id", 1, "category", "infrastructura", "status", "NEW", "submittedBy", authentication.getName()),
                Map.of("id", 2, "category", "iluminat", "status", "IN_PROGRESS", "submittedBy", authentication.getName())),
                "Sesizarile utilizatorului curent.");
    }

    @PostMapping
    public ApiResponse<CityReportResponse> submit(
            @RequestBody CityReportRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String email = currentUser.getUsername();

        return ApiResponse.success(cityReportService.createReport(request, email), "Raport trimis.");
    }
}
