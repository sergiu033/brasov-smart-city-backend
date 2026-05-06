package com.smartcity.reports.dto;

import com.smartcity.reports.enums.ReportStatus;

import java.time.LocalDateTime;

public record CityReportRequest(
        Long id,
        Long categoryId,
        String description,
        Double latitude,
        Double longitude,
        String photoUrl,
        ReportStatus status,
        LocalDateTime createdAt
) {
}
