package com.smartcity.parking.mapper;

import com.smartcity.parking.dto.request.ParkingZoneCreateRequest;
import com.smartcity.parking.dto.response.ParkingZoneDetailsResponse;
import com.smartcity.parking.entity.ParkingZone;
import com.smartcity.parking.enums.ParkingZoneStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ParkingZoneMapperImpl.class)
class ParkingZoneMapperTest {

    @Autowired
    private ParkingZoneMapper parkingZoneMapper;

    @Test
    void parkingZoneCreateRequestToParkingZone_mapsFields() {
        ParkingZoneCreateRequest request = ParkingZoneCreateRequest.builder()
                .zoneCode("Z1")
                .tariffPerHour(new BigDecimal("5.00"))
                .tariffPerDay(new BigDecimal("30.00"))
                .status(ParkingZoneStatus.ACTIVE)
                .build();

        ParkingZone zone = parkingZoneMapper.parkingZoneCreateRequestToParkingZone(request);
        zone.setId(1L);

        ParkingZoneDetailsResponse response = parkingZoneMapper.parkingZoneToParkingZoneDetailsResponse(zone);

        assertThat(zone.getZoneCode()).isEqualTo("Z1");
        assertThat(response.zoneCode()).isEqualTo("Z1");
        assertThat(response.tariffPerHour()).isEqualByComparingTo("5.00");
    }
}
