package com.smartcity.event.dto.response;

import lombok.Builder;

@Builder
public record EventResponse(
        Long id,
        String title,
        String when,
        String location,
        String imageUrl
) {
}
