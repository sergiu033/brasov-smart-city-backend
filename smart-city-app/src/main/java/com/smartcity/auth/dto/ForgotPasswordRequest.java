package com.smartcity.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "Email-ul este obligatoriu.")
        @Email(message = "Email-ul nu are format valid.")
        String email) {
}
