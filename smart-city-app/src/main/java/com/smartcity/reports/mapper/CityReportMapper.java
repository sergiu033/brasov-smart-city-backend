package com.smartcity.reports.mapper;

import com.smartcity.reports.dto.CityReportRequest;
import com.smartcity.reports.dto.CityReportResponse;
import com.smartcity.reports.entity.CityReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CityReportMapper {

    @Mapping(target = "userName", expression = "java(mapUserName(cityReport))")
    @Mapping(source = "category.name", target = "categoryName")
    CityReportResponse toResponse(CityReport cityReport);

    @Mapping(target = "photoUrl", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    CityReport toEntity(CityReportRequest cityReportRequest);

    default String mapUserName(CityReport cityReport) {
        if (Boolean.TRUE.equals(cityReport.getAnonymous())) {
            return "anonim";
        }
        return cityReport.getUser() != null ? cityReport.getUser().getFullName() : null;
    }
}
