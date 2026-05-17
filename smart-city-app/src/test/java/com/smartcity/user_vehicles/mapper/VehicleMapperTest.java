package com.smartcity.user_vehicles.mapper;

import com.smartcity.user.entity.User;
import com.smartcity.user_vehicles.dto.VehicleResponse;
import com.smartcity.user_vehicles.entity.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = VehicleMapperImpl.class)
class VehicleMapperTest {

    @Autowired
    private VehicleMapper vehicleMapper;

    @Test
    void vehicleToVehicleResponse_mapsFields() {
        User user = new User();
        user.setId(1L);
        Vehicle vehicle = Vehicle.builder()
                .id(5L)
                .user(user)
                .plateNumber("BV 12 XYZ")
                .build();

        VehicleResponse response = vehicleMapper.vehicleToVehicleResponse(vehicle);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.plateNumber()).isEqualTo("BV 12 XYZ");
    }
}
