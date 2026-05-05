package com.smartcity.reports.mapper;

import com.smartcity.reports.dto.CityReportRequest;
import com.smartcity.reports.dto.CityReportResponse;
import com.smartcity.reports.entity.CityReport;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CityReportMapper {

    CityReportResponse toResponse(CityReport cityReport);
    CityReport toEntity(CityReportRequest cityReportRequest);
}
