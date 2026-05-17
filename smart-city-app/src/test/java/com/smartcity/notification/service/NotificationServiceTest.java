package com.smartcity.notification.service;

import com.smartcity.notification.dto.response.NotificationResponse;
import com.smartcity.notification.entity.Notification;
import com.smartcity.notification.enums.NotificationType;
import com.smartcity.notification.repository.NotificationRepository;
import com.smartcity.user.entity.Role;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendNotification_savesNotification() {
        User user = sampleUser();
        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));

        notificationService.sendNotification(
                "ion@example.com", "Titlu", "Mesaj", NotificationType.REPORT_STATUS_CHANGE);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void sendNotification_throwsWhenUserMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.sendNotification(
                        "missing@example.com", "T", "M", NotificationType.REPORT_STATUS_CHANGE))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void markAsRead_throwsWhenUnauthorized() {
        User owner = sampleUser();
        User other = new User();
        other.setEmail("other@example.com");
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setUser(owner);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.markAsRead(1L, "other@example.com"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getUnreadCount_returnsCount() {
        User user = sampleUser();
        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.countByUserAndReadFalse(user)).thenReturn(3L);

        assertThat(notificationService.getUnreadCount("ion@example.com")).isEqualTo(3L);
    }

    @Test
    void getUserNotifications_returnsPage() {
        User user = sampleUser();
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setTitle("T");
        notification.setMessage("M");
        notification.setType(NotificationType.REPORT_STATUS_CHANGE);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserOrderByCreatedAtDesc(eq(user), any()))
                .thenReturn(new PageImpl<>(List.of(notification)));

        Page<NotificationResponse> page =
                notificationService.getUserNotifications("ion@example.com", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
    }

    private static User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ion@example.com");
        user.setRole(Role.CITIZEN);
        return user;
    }
}
