package com.smartcity.user.dto;

public record UserProfileResponse(
        Long id,
        String fullName,
        String email,
        String role,
        String lastLogin,
        String profilePictureUrl) {
}
