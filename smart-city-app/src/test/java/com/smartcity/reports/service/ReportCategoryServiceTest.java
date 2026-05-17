package com.smartcity.reports.service;

import com.smartcity.exception.ReportCategoryNotFoundException;
import com.smartcity.reports.dto.ReportCategoryRequest;
import com.smartcity.reports.dto.ReportCategoryResponse;
import com.smartcity.reports.entity.ReportCategory;
import com.smartcity.reports.mapper.ReportCategoryMapper;
import com.smartcity.reports.repository.ReportCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportCategoryServiceTest {

    @Mock
    private ReportCategoryRepository reportCategoryRepository;
    @Mock
    private ReportCategoryMapper reportCategoryMapper;

    @InjectMocks
    private ReportCategoryService reportCategoryService;

    @Test
    void createCategory_savesAndReturnsResponse() {
        ReportCategoryRequest request = new ReportCategoryRequest("Drumuri");
        ReportCategory entity = new ReportCategory();
        entity.setName("Drumuri");
        ReportCategory saved = new ReportCategory();
        saved.setId(1L);
        saved.setName("Drumuri");
        ReportCategoryResponse response = new ReportCategoryResponse(1L, "Drumuri");

        when(reportCategoryMapper.toEntity(request)).thenReturn(entity);
        when(reportCategoryRepository.save(entity)).thenReturn(saved);
        when(reportCategoryMapper.toResponse(saved)).thenReturn(response);

        assertThat(reportCategoryService.createCategory(request)).isEqualTo(response);
    }

    @Test
    void getCategoryById_throwsWhenMissing() {
        when(reportCategoryRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportCategoryService.getCategoryById(9L))
                .isInstanceOf(ReportCategoryNotFoundException.class);
    }

    @Test
    void getAllCategories_returnsMappedList() {
        ReportCategory category = new ReportCategory();
        category.setId(1L);
        category.setName("Iluminat");
        when(reportCategoryRepository.findAll()).thenReturn(List.of(category));
        when(reportCategoryMapper.toResponse(category)).thenReturn(new ReportCategoryResponse(1L, "Iluminat"));

        assertThat(reportCategoryService.getAllCategories()).hasSize(1);
    }

    @Test
    void deleteCategory_deletesById() {
        reportCategoryService.deleteCategory(3L);
        verify(reportCategoryRepository).deleteById(3L);
    }
}
