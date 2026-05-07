package com.smartcity.recommendation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecommendationCategoryUpdateRequest(
        @NotNull(message = "Id-ul este obligatoriu.")
        Long id,
        @NotBlank(message = "Codul este obligatoriu.")
        String code,
        @NotBlank(message = "Numele este obligatoriu.")
        String displayName
) {
}
