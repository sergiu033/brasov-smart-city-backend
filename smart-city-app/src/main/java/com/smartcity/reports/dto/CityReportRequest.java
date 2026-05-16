package com.smartcity.reports.dto;

import com.smartcity.reports.enums.ReportStatus;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

public record CityReportRequest(
        Long id,
        Long categoryId,
        String description,
        Double latitude,
        Double longitude,
        MultipartFile image,
        ReportStatus status,
        LocalDateTime createdAt
) {
}
