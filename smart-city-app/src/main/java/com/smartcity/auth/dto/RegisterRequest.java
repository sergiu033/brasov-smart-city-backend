package com.smartcity.auth.dto;

import com.smartcity.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Numele este obligatoriu.")
        String fullName,
        @NotBlank(message = "Email-ul este obligatoriu.")
        @Email(message = "Email-ul nu are format valid.")
        String email,
        @NotBlank(message = "Parola este obligatorie.")
        @Size(min = 8, message = "Parola trebuie sa aiba cel putin 8 caractere.")
        String password,
        @NotBlank(message = "Confirmarea parolei este obligatorie.")
        String confirmPassword,
        Role role) {
}
