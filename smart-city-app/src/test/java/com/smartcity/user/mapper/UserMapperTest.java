package com.smartcity.user.mapper;

import com.smartcity.user.dto.UserProfileResponse;
import com.smartcity.user.entity.Role;
import com.smartcity.user.entity.User;
import com.smartcity.user_vehicles.entity.Vehicle;
import com.smartcity.user_vehicles.mapper.VehicleMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class, VehicleMapperImpl.class})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void toDto_mapsUserFields() {
        LocalDateTime lastLogin = LocalDateTime.of(2026, 5, 17, 10, 30);
        User user = new User();
        user.setId(1L);
        user.setFullName("Ion Popescu");
        user.setEmail("ion@example.com");
        user.setRole(Role.CITIZEN);
        user.setLastLogin(lastLogin);
        user.setProfilePictureUrl("2026/0517/avatar.png");
        user.setVehicles(new LinkedHashSet<>());

        UserProfileResponse response = userMapper.toDto(user);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.fullName()).isEqualTo("Ion Popescu");
        assertThat(response.email()).isEqualTo("ion@example.com");
        assertThat(response.role()).isEqualTo("CITIZEN");
        assertThat(response.profilePictureUrl()).isEqualTo("2026/0517/avatar.png");
        assertThat(response.lastLogin()).isEqualTo("2026-05-17T10:30:00");
        assertThat(response.vehicles()).isEmpty();
    }

    @Test
    void toDto_mapsVehicles() {
        User user = new User();
        user.setId(2L);
        user.setFullName("Maria Ionescu");
        user.setEmail("maria@example.com");
        user.setRole(Role.TOURIST);

        Vehicle vehicle = Vehicle.builder()
                .id(10L)
                .user(user)
                .plateNumber("BV 01 ABC")
                .build();
        user.setVehicles(new LinkedHashSet<>(Set.of(vehicle)));

        UserProfileResponse response = userMapper.toDto(user);

        assertThat(response.vehicles()).hasSize(1);
        assertThat(response.vehicles().iterator().next().id()).isEqualTo(10L);
        assertThat(response.vehicles().iterator().next().plateNumber()).isEqualTo("BV 01 ABC");
    }
}
