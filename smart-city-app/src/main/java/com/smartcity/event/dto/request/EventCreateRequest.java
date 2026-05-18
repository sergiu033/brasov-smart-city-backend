package com.smartcity.event.dto.request;

import com.smartcity.event.enums.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record EventCreateRequest(
        @NotBlank(message = "Titlul nu poate fi gol.")
        @Size(max = 255, message = "Titlul nu poate depasi 255 de caractere.")
        @Pattern(regexp = "^[^<>]*$", message = "Titlul conține caractere nepermise.")
        String title,
        @NotBlank(message = "Descrierea nu poate fi goala.")
        @Size(max = 2000, message = "Descrierea nu poate depasi 2000 de caractere.")
        @Pattern(regexp = "^[^<>]*$", message = "Descrierea conține caractere nepermise.")
        String description,
        @NotBlank(message = "Locatia nu poate fi goala.")
        @Size(max = 255, message = "Locatia nu poate depasi 255 de caractere.")
        @Pattern(regexp = "^[^<>]*$", message = "Locația conține caractere nepermise.")
        String location,
        @NotNull(message = "Data de inceput este obligatorie.")
        LocalDateTime startTime,
        @NotNull(message = "Data de sfarsit este obligatorie.")
        LocalDateTime endTime,
        EventStatus status,
        MultipartFile image
) {
}
