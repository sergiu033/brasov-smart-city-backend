package com.smartcity.recommendation.dto.response;

import lombok.Builder;

@Builder
public record RecommendationCategoryResponse(
        Long id,
        String code,
        String displayName
) {
}
