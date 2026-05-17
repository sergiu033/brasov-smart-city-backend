package com.smartcity.reports.mapper;

import com.smartcity.reports.dto.CityReportResponse;
import com.smartcity.reports.entity.CityReport;
import com.smartcity.reports.entity.ReportCategory;
import com.smartcity.reports.enums.ReportStatus;
import com.smartcity.user.entity.Role;
import com.smartcity.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CityReportMapperImpl.class)
class CityReportMapperTest {

    @Autowired
    private CityReportMapper cityReportMapper;

    @Test
    void toResponse_mapsUserAndCategory() {
        User user = new User();
        user.setId(10L);
        user.setFullName("Ion Popescu");
        user.setEmail("ion@example.com");
        user.setRole(Role.CITIZEN);

        ReportCategory category = new ReportCategory();
        category.setId(2L);
        category.setName("Iluminat");

        CityReport report = CityReport.builder()
                .id(1L)
                .user(user)
                .category(category)
                .description("Lampa defecta")
                .latitude(45.65)
                .longitude(25.61)
                .status(ReportStatus.NEW)
                .build();

        CityReportResponse response = cityReportMapper.toResponse(report);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(10L);
        assertThat(response.userName()).isEqualTo("Ion Popescu");
        assertThat(response.categoryName()).isEqualTo("Iluminat");
        assertThat(response.description()).isEqualTo("Lampa defecta");
    }
}
