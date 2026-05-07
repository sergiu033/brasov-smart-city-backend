package com.smartcity.recommendation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RecommendationUpdateRequest(
        Long id,
        @NotBlank(message = "Titlul este obligatoriu.")
        String title,
        String location,
        String description,
        Long recommendationCategoryId
) {
}
