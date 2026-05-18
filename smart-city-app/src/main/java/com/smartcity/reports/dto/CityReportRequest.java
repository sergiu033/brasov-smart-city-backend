package com.smartcity.reports.dto;

import com.smartcity.reports.enums.ReportStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

public record CityReportRequest(
        Long id,
        @NotNull(message = "Categoria este obligatorie.")
        Long categoryId,
        @NotBlank(message = "Descrierea este obligatorie.")
        @Size(max = 1000, message = "Descrierea nu poate depasi 1000 de caractere.")
        @Pattern(regexp = "^[^<>]*$", message = "Descrierea conține caractere nepermise.")
        String description,
        @NotNull(message = "Latitudinea este obligatorie.")
        Double latitude,
        @NotNull(message = "Longitudinea este obligatorie.")
        Double longitude,
        MultipartFile image,
        ReportStatus status,
        LocalDateTime createdAt
) {
}
