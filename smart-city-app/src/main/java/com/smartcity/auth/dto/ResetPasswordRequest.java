package com.smartcity.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Token-ul de reset este obligatoriu.")
        String token,
        @NotBlank(message = "Parola noua este obligatorie.")
        @Size(min = 8, message = "Parola trebuie sa aiba cel putin 8 caractere.")
        String newPassword,
        @NotBlank(message = "Confirmarea parolei este obligatorie.")
        String confirmPassword) {
}
