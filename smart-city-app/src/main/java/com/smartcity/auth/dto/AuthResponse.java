package com.smartcity.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        String role,
        String fullName) {
}
