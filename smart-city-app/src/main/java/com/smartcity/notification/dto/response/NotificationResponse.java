package com.smartcity.notification.dto.response;

import com.smartcity.notification.enums.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        NotificationType type,
        boolean read,
        LocalDateTime createdAt) {
}
