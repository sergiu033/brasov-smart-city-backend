package com.smartcity.recommendation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RecommendationCategoryCreateRequest(
        @NotBlank(message = "Codul este obligatoriu.")
        String code,
        @NotBlank(message = "Numele este obligatoriu.")
        String displayName
) {
}
