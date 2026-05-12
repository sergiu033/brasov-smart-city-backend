package com.smartcity.reports.dto;

import com.smartcity.reports.enums.ReportStatus;

import java.time.LocalDateTime;

public record CityReportResponse(
        Long id,
        Long userId,
        String userName,
        ReportCategoryResponse category,
        String description,
        Double latitude,
        Double longitude,
        String photoUrl,
        ReportStatus status,
        LocalDateTime createdAt
) {
}
