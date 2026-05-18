package com.smartcity.reports.controller;

import com.smartcity.reports.dto.CityReportRequest;
import com.smartcity.reports.dto.CityReportResponse;
import com.smartcity.reports.enums.ReportStatus;
import com.smartcity.reports.service.CityReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportsControllerTest {

    @Mock
    private CityReportService cityReportService;

    @InjectMocks
    private ReportsController reportsController;

    @Test
    void submit_returnsCreated() {
        CityReportRequest request = new CityReportRequest(null, 1L, "desc", 1.0, 2.0, null, ReportStatus.NEW, false, null);
        CityReportResponse response = new CityReportResponse(1L, "Ion", "Cat", "desc", 1.0, 2.0, null, ReportStatus.NEW, false, null);
        User user = new User("ion@example.com", "pwd", List.of());

        when(cityReportService.createReport(request, "ion@example.com")).thenReturn(response);

        ResponseEntity<CityReportResponse> result = reportsController.submit(request, user);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getReport_delegatesToService() {
        CityReportResponse response = new CityReportResponse(1L, "Ion", "Cat", "d", null, null, null, ReportStatus.NEW, false, null);
        when(cityReportService.getReportById(1L)).thenReturn(response);

        assertThat(reportsController.getReport(1L).getBody()).isEqualTo(response);
    }

    @Test
    void deleteReport_returnsNoContent() {
        ResponseEntity<Void> result = reportsController.deleteReport(3L);

        verify(cityReportService).deleteReport(3L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getReports_delegatesToService() {
        Page<CityReportResponse> page = new PageImpl<>(List.of());
        PageRequest pageable = PageRequest.of(0, 10);
        when(cityReportService.getAllReports(2L, pageable)).thenReturn(page);

        assertThat(reportsController.getReports(2L, pageable).getBody()).isEqualTo(page);
    }
}
