package com.smartcity.reports.mapper;

import com.smartcity.reports.dto.ReportCategoryRequest;
import com.smartcity.reports.dto.ReportCategoryResponse;
import com.smartcity.reports.entity.ReportCategory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReportCategoryMapperTest {

    private final ReportCategoryMapper mapper = new ReportCategoryMapper();

    @Test
    void toEntity_mapsName() {
        ReportCategory entity = mapper.toEntity(new ReportCategoryRequest("Iluminat"));

        assertThat(entity.getName()).isEqualTo("Iluminat");
    }

    @Test
    void toResponse_mapsIdAndName() {
        ReportCategory entity = new ReportCategory();
        entity.setId(3L);
        entity.setName("Drumuri");

        ReportCategoryResponse response = mapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(3L);
        assertThat(response.name()).isEqualTo("Drumuri");
    }
}
