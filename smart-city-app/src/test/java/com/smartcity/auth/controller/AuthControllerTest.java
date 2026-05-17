package com.smartcity.auth.controller;

import com.smartcity.auth.dto.*;
import com.smartcity.auth.service.AuthService;
import com.smartcity.user.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_delegatesToService() {
        RegisterRequest request = new RegisterRequest("Ion", "ion@example.com", "pass1234", "pass1234", Role.CITIZEN);
        AuthResponse response = new AuthResponse("a", "r", "Bearer", "CITIZEN", "Ion");
        when(authService.register(request)).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.register(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void login_delegatesToService() {
        LoginRequest request = new LoginRequest("ion@example.com", "pass1234");
        AuthResponse response = new AuthResponse("a", "r", "Bearer", "CITIZEN", "Ion");
        when(authService.login(request)).thenReturn(response);

        assertThat(authController.login(request).getBody()).isEqualTo(response);
    }

    @Test
    void logout_delegatesToService() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh");

        ResponseEntity<Void> result = authController.logout(request);

        verify(authService).logout("refresh");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
