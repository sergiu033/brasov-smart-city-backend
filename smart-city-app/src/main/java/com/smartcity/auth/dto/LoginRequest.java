package com.smartcity.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "Email-ul este obligatoriu.")
        @Email(message = "Email-ul nu are format valid.")
        @Pattern(regexp = "^[^<>]*$", message = "Email-ul conține caractere nepermise.")
        String email,
        @NotBlank(message = "Parola este obligatorie.")
        String password) {
}
