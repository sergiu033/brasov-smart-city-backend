package com.smartcity.reports.mapper;

import com.smartcity.reports.dto.ReportCategoryRequest;
import com.smartcity.reports.dto.ReportCategoryResponse;
import com.smartcity.reports.entity.ReportCategory;
import org.springframework.stereotype.Component;

@Component
public class ReportCategoryMapper {

    public ReportCategory toEntity(ReportCategoryRequest request) {
        ReportCategory entity = new ReportCategory();
        entity.setName(request.name());
        return entity;
    }

    public ReportCategoryResponse toResponse(ReportCategory entity) {
        return new ReportCategoryResponse(entity.getId(), entity.getName());
    }
}
