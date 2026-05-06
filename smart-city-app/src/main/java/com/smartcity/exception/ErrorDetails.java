package com.smartcity.exception;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ErrorDetails(
        String message,
        OffsetDateTime timestamp,
        String details
) {
}
