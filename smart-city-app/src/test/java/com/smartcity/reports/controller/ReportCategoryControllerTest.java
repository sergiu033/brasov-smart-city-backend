package com.smartcity.reports.controller;

import com.smartcity.reports.dto.ReportCategoryRequest;
import com.smartcity.reports.dto.ReportCategoryResponse;
import com.smartcity.reports.service.ReportCategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportCategoryControllerTest {

    @Mock
    private ReportCategoryService reportCategoryService;

    @InjectMocks
    private ReportCategoryController reportCategoryController;

    @Test
    void createCategory_returnsCreated() {
        ReportCategoryRequest request = new ReportCategoryRequest("Drumuri");
        ReportCategoryResponse response = new ReportCategoryResponse(1L, "Drumuri");
        when(reportCategoryService.createCategory(request)).thenReturn(response);

        ResponseEntity<ReportCategoryResponse> result = reportCategoryController.createCategory(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getAllCategories_delegatesToService() {
        when(reportCategoryService.getAllCategories()).thenReturn(List.of(new ReportCategoryResponse(1L, "A")));

        assertThat(reportCategoryController.getAllCategories().getBody()).hasSize(1);
    }

    @Test
    void deleteCategory_returnsNoContent() {
        ResponseEntity<Void> result = reportCategoryController.deleteCategory(2L);

        verify(reportCategoryService).deleteCategory(2L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
