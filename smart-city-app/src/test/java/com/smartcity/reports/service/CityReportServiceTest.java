package com.smartcity.reports.service;

import com.smartcity.exception.ReportCategoryNotFoundException;
import com.smartcity.exception.UserNotFoundException;
import com.smartcity.notification.service.NotificationService;
import com.smartcity.reports.dto.CityReportRequest;
import com.smartcity.reports.dto.CityReportResponse;
import com.smartcity.reports.entity.CityReport;
import com.smartcity.reports.entity.ReportCategory;
import com.smartcity.reports.enums.ReportStatus;
import com.smartcity.reports.mapper.CityReportMapper;
import com.smartcity.reports.repository.CityReportRepository;
import com.smartcity.reports.repository.ReportCategoryRepository;
import com.smartcity.imagestorage.service.ImageService;
import com.smartcity.user.entity.Role;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityReportServiceTest {

    @Mock
    private CityReportMapper cityReportMapper;
    @Mock
    private CityReportRepository cityReportRepository;
    @Mock
    private ReportCategoryRepository reportCategoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private CityReportService cityReportService;

    @Test
    void createReport_throwsWhenUserMissing() {
        CityReportRequest request = new CityReportRequest(null, 1L, "desc", 1.0, 2.0, null, ReportStatus.NEW, null);
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityReportService.createReport(request, "missing@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void createReport_throwsWhenCategoryMissing() {
        User user = sampleUser();
        CityReportRequest request = new CityReportRequest(null, 99L, "desc", 1.0, 2.0, null, ReportStatus.NEW, null);

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(reportCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityReportService.createReport(request, "ion@example.com"))
                .isInstanceOf(ReportCategoryNotFoundException.class);
    }

    @Test
    void createReport_savesAndNotifies() {
        User user = sampleUser();
        ReportCategory category = new ReportCategory();
        category.setId(1L);
        category.setName("Drumuri");
        CityReportRequest request = new CityReportRequest(null, 1L, "Groapa", 45.6, 25.6, null, ReportStatus.NEW, null);
        CityReport entity = CityReport.builder().description("Groapa").build();
        CityReport saved = CityReport.builder().id(10L).user(user).category(category).build();
        CityReportResponse response = new CityReportResponse(
                10L, 1L, "Ion Popescu", "Drumuri", "Groapa", 45.6, 25.6, null, ReportStatus.NEW, null);

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(reportCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(cityReportMapper.toEntity(request)).thenReturn(entity);
        when(cityReportRepository.save(entity)).thenReturn(saved);
        when(cityReportMapper.toResponse(saved)).thenReturn(response);

        cityReportService.createReport(request, "ion@example.com");

        verify(notificationService).sendNotification(eq("ion@example.com"), any(), any(), any());
        verify(cityReportRepository).save(entity);
    }

    private static User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ion@example.com");
        user.setFullName("Ion Popescu");
        user.setRole(Role.CITIZEN);
        return user;
    }
}
