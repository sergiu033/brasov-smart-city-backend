package com.smartcity.parking.controller;

import com.smartcity.parking.dto.request.ParkingZoneCreateRequest;
import com.smartcity.parking.dto.response.ParkingZoneDetailsResponse;
import com.smartcity.parking.dto.response.ParkingZoneResponse;
import com.smartcity.parking.service.ParkingZoneService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingZoneControllerTest {

    @Mock
    private ParkingZoneService parkingZoneService;

    @InjectMocks
    private ParkingZoneController parkingZoneController;

    @Test
    void zones_delegatesToService() {
        Page<ParkingZoneResponse> page = new PageImpl<>(List.of());
        PageRequest pageable = PageRequest.of(0, 10);
        when(parkingZoneService.findAll(pageable)).thenReturn(page);

        assertThat(parkingZoneController.zones(pageable).getBody()).isEqualTo(page);
    }

    @Test
    void zoneDetails_delegatesToService() {
        ParkingZoneDetailsResponse details =
                ParkingZoneDetailsResponse.builder().zoneCode("Z1").tariffPerHour(new BigDecimal("5")).build();
        when(parkingZoneService.findByZoneCode("Z1")).thenReturn(details);

        assertThat(parkingZoneController.zoneDetails("Z1").getBody()).isEqualTo(details);
    }

    @Test
    void deleteParkingZone_returnsNoContent() {
        ResponseEntity<Void> result = parkingZoneController.deleteParkingZone(3L);

        verify(parkingZoneService).deleteParkingZone(3L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
