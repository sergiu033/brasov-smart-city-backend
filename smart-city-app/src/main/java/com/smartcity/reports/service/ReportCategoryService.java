package com.smartcity.reports.service;

import com.smartcity.common.exception.ReportCategoryNotFoundException;
import com.smartcity.reports.dto.ReportCategoryRequest;
import com.smartcity.reports.dto.ReportCategoryResponse;
import com.smartcity.reports.entity.ReportCategory;
import com.smartcity.reports.mapper.ReportCategoryMapper;
import com.smartcity.reports.repository.ReportCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportCategoryService {

    private final ReportCategoryRepository reportCategoryRepository;
    private final ReportCategoryMapper reportCategoryMapper;

    @Transactional
    public ReportCategoryResponse createCategory(ReportCategoryRequest request) {
        ReportCategory entity = reportCategoryMapper.toEntity(request);
        return reportCategoryMapper.toResponse(reportCategoryRepository.save(entity));
    }

    public ReportCategoryResponse getCategoryById(Long id) {
        ReportCategory category = reportCategoryRepository.findById(id)
                .orElseThrow(() -> new ReportCategoryNotFoundException("Report category with id: " + id + " not found"));
        return reportCategoryMapper.toResponse(category);
    }

    public List<ReportCategoryResponse> getAllCategories() {
        return reportCategoryRepository.findAll().stream()
                .map(reportCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportCategoryResponse updateCategory(Long id, ReportCategoryRequest request) {
        ReportCategory category = reportCategoryRepository.findById(id)
                .orElseThrow(() -> new ReportCategoryNotFoundException("Report category with id: " + id + " not found"));
        
        category.setName(request.name());
        return reportCategoryMapper.toResponse(reportCategoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        reportCategoryRepository.deleteById(id);
    }
}
