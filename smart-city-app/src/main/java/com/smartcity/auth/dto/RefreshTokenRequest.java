package com.smartcity.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token este obligatoriu.")
        String refreshToken) {
}
