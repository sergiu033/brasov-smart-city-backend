package com.smartcity.reports.dto;

import com.smartcity.reports.enums.ReportStatus;

import java.time.LocalDateTime;

public record CityReportResponse(
        Long id,
        String userName,
        String categoryName,
        String description,
        Double latitude,
        Double longitude,
        String photoUrl,
        ReportStatus status,
        Boolean anonymous,
        LocalDateTime createdAt
) {
}
