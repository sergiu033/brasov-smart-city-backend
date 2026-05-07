package com.smartcity.recommendation.dto.response;

import lombok.Builder;

@Builder
public record RecommendationResponse(
        Long id,
        String category,
        String title,
        String location
) {
}
