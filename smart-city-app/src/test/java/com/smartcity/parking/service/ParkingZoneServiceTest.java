package com.smartcity.parking.service;

import com.smartcity.exception.ParkingZoneCodeAlreadyTakenException;
import com.smartcity.exception.ParkingZoneNotFoundException;
import com.smartcity.parking.dto.request.ParkingZoneCreateRequest;
import com.smartcity.parking.dto.response.ParkingZoneDetailsResponse;
import com.smartcity.parking.entity.ParkingZone;
import com.smartcity.parking.enums.ParkingZoneStatus;
import com.smartcity.parking.mapper.ParkingZoneMapper;
import com.smartcity.parking.repository.ParkingZoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingZoneServiceTest {

    @Mock
    private ParkingZoneRepository parkingZoneRepository;
    @Mock
    private ParkingZoneMapper parkingZoneMapper;

    @InjectMocks
    private ParkingZoneService parkingZoneService;

    @Test
    void findByZoneCode_throwsWhenMissing() {
        when(parkingZoneRepository.findByZoneCode("Z9")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parkingZoneService.findByZoneCode("Z9"))
                .isInstanceOf(ParkingZoneNotFoundException.class);
    }

    @Test
    void addParkingZone_throwsWhenCodeTaken() {
        ParkingZoneCreateRequest request = ParkingZoneCreateRequest.builder()
                .zoneCode("Z1")
                .tariffPerHour(new BigDecimal("5"))
                .tariffPerDay(new BigDecimal("20"))
                .status(ParkingZoneStatus.ACTIVE)
                .build();
        ParkingZone mapped = ParkingZone.builder().zoneCode("Z1").build();

        when(parkingZoneMapper.parkingZoneCreateRequestToParkingZone(request)).thenReturn(mapped);
        when(parkingZoneRepository.existsByZoneCode("Z1")).thenReturn(true);

        assertThatThrownBy(() -> parkingZoneService.addParkingZone(request))
                .isInstanceOf(ParkingZoneCodeAlreadyTakenException.class);
    }

    @Test
    void addParkingZone_savesZone() {
        ParkingZoneCreateRequest request = ParkingZoneCreateRequest.builder()
                .zoneCode("Z1")
                .tariffPerHour(new BigDecimal("5"))
                .tariffPerDay(new BigDecimal("20"))
                .status(ParkingZoneStatus.ACTIVE)
                .build();
        ParkingZone mapped = ParkingZone.builder().zoneCode("Z1").build();
        ParkingZone saved = ParkingZone.builder().id(1L).zoneCode("Z1").build();
        ParkingZoneDetailsResponse details = ParkingZoneDetailsResponse.builder().zoneCode("Z1").build();

        when(parkingZoneMapper.parkingZoneCreateRequestToParkingZone(request)).thenReturn(mapped);
        when(parkingZoneRepository.existsByZoneCode("Z1")).thenReturn(false);
        when(parkingZoneRepository.save(mapped)).thenReturn(saved);
        when(parkingZoneMapper.parkingZoneToParkingZoneDetailsResponse(saved)).thenReturn(details);

        parkingZoneService.addParkingZone(request);

        verify(parkingZoneRepository).save(mapped);
    }
}
