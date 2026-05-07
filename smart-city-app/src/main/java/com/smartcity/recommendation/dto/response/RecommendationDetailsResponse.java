package com.smartcity.recommendation.dto.response;

import lombok.Builder;

@Builder
public record RecommendationDetailsResponse(
        Long id,
        String title,
        String location,
        String description
) {
}
