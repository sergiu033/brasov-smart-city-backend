package com.smartcity.reports.service;

import com.smartcity.reports.dto.CityReportRequest;
import com.smartcity.reports.dto.CityReportResponse;
import com.smartcity.reports.entity.CityReport;
import com.smartcity.reports.mapper.CityReportMapper;
import com.smartcity.reports.repository.CityReportRepository;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityReportService {

    private final CityReportMapper cityReportMapper;
    private final CityReportRepository cityReportRepository;
    private final UserRepository userRepository;

    public CityReportResponse createReport(CityReportRequest cityReportRequest, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userEmail));

        CityReport newReport = cityReportMapper.toEntity(cityReportRequest);

        newReport.setUser(user);

        return cityReportMapper.toResponse(
                cityReportRepository.save(newReport)
        );
    }
}
