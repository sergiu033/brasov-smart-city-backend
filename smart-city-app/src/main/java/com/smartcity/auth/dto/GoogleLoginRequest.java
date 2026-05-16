package com.smartcity.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "Token-ul Google este obligatoriu.")
        String idToken) {
}
