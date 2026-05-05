package com.smartcity.event.dto.request;

import com.smartcity.event.enums.EventStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record EventCreateRequest(
        @NotBlank(message = "Titlul nu poate fi gol.")
        String title,
        String description,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        EventStatus status
) {
}
