package com.smartcity.event.dto.response;

import com.smartcity.event.enums.EventStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventDetailsResponse(
        Long id,
        String title,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        EventStatus status,
        String imageUrl
) {}
