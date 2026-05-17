package com.smartcity.user.dto;

import com.smartcity.user_vehicles.dto.VehicleResponse;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserProfileResponse(
        Long id,
        String fullName,
        String email,
        String role,
        String lastLogin,
        String profilePictureUrl,
        Set<VehicleResponse> vehicles
) {
}
