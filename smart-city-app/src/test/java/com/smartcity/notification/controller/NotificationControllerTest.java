package com.smartcity.notification.controller;

import com.smartcity.notification.dto.response.NotificationResponse;
import com.smartcity.notification.enums.NotificationType;
import com.smartcity.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void getNotifications_delegatesToService() {
        var auth = UsernamePasswordAuthenticationToken.authenticated("ion@example.com", null, List.of());
        PageRequest pageable = PageRequest.of(0, 10);
        Page<NotificationResponse> page = new PageImpl<>(List.of(
                new NotificationResponse(1L, "T", "M", NotificationType.REPORT_STATUS_CHANGE, false, LocalDateTime.now())));
        when(notificationService.getUserNotifications("ion@example.com", pageable)).thenReturn(page);

        assertThat(notificationController.getNotifications(auth, pageable).getBody()).isEqualTo(page);
    }

    @Test
    void getUnreadCount_delegatesToService() {
        var auth = UsernamePasswordAuthenticationToken.authenticated("ion@example.com", null, List.of());
        when(notificationService.getUnreadCount("ion@example.com")).thenReturn(5L);

        assertThat(notificationController.getUnreadCount(auth).getBody()).isEqualTo(5L);
    }

    @Test
    void markAsRead_delegatesToService() {
        var auth = UsernamePasswordAuthenticationToken.authenticated("ion@example.com", null, List.of());

        notificationController.markAsRead(1L, auth);

        verify(notificationService).markAsRead(1L, "ion@example.com");
    }
}
