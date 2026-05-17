package com.smartcity.reports.controller;

import com.smartcity.reports.dto.CityReportRequest;
import com.smartcity.reports.dto.CityReportResponse;
import com.smartcity.reports.service.CityReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final CityReportService cityReportService;

    public ReportsController(CityReportService cityReportService) {
        this.cityReportService = cityReportService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CityReportResponse> submit(
            @ModelAttribute CityReportRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String email = currentUser.getUsername();

        return ResponseEntity.status(HttpStatus.CREATED).body(cityReportService.createReport(request, email));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<CityReportResponse>> currentUserReports(
            @AuthenticationPrincipal UserDetails currentUser,
            Pageable pageable
    ) {
        String email = currentUser.getUsername();

        return ResponseEntity.ok().body(cityReportService.getCurrentUserReports(email, pageable));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<CityReportResponse> getReport(
            @PathVariable Long reportId
    ) {
        return ResponseEntity.ok().body(cityReportService.getReportById(reportId));
    }

    @GetMapping
    public ResponseEntity<Page<CityReportResponse>> getReports(
            @RequestParam(required = false) Long category,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(cityReportService.getAllReports(category, pageable));
    }

    @PutMapping(value = "/{reportId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CityReportResponse> updateReport(
            @PathVariable Long reportId,
            @ModelAttribute CityReportRequest request
    ) {
        return ResponseEntity.ok().body(cityReportService.updateReport(reportId, request));
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable Long reportId
    ) {
        cityReportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}
