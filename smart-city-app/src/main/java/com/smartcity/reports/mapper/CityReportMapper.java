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

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userName")
    CityReportResponse toResponse(CityReport cityReport);

    CityReport toEntity(CityReportRequest cityReportRequest);
}
