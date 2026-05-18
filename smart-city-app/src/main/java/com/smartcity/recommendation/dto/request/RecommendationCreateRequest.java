package com.smartcity.recommendation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RecommendationCreateRequest(
        @NotBlank(message = "Titlul este obligatoriu.")
        @Size(max = 255, message = "Titlul nu poate depasi 255 de caractere.")
        @Pattern(regexp = "^[^<>]*$", message = "Titlul conține caractere nepermise.")
        String title,
        @NotBlank(message = "Locatia este obligatorie.")
        @Size(max = 255, message = "Locatia nu poate depasi 255 de caractere.")
        @Pattern(regexp = "^[^<>]*$", message = "Locația conține caractere nepermise.")
        String location,
        @NotBlank(message = "Descrierea este obligatorie.")
        @Size(max = 2000, message = "Descrierea nu poate depasi 2000 de caractere.")
        @Pattern(regexp = "^[^<>]*$", message = "Descrierea conține caractere nepermise.")
        String description,
        @NotNull(message = "Categoria este obligatorie.")
        Long recommendationCategoryId
) {
}
