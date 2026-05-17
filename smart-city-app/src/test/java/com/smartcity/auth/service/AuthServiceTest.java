package com.smartcity.auth.service;

import com.smartcity.auth.dto.AuthResponse;
import com.smartcity.auth.dto.LoginRequest;
import com.smartcity.auth.dto.RegisterRequest;
import com.smartcity.auth.dto.ResetPasswordRequest;
import com.smartcity.auth.repository.RefreshTokenRepository;
import com.smartcity.security.JwtService;
import com.smartcity.user.entity.Role;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private GoogleTokenVerifier googleTokenVerifier;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                authenticationManager,
                jwtService,
                googleTokenVerifier,
                7);
    }

    @Test
    void register_createsUserAndReturnsTokens() {
        RegisterRequest request = new RegisterRequest(
                "Ion Popescu", "ion@example.com", "password1", "password1", null);
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("ion@example.com");
        savedUser.setFullName("Ion Popescu");
        savedUser.setRole(Role.CITIZEN);
        savedUser.setPasswordHash("encoded");

        when(userRepository.existsByEmail("ion@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password1")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(any(), any())).thenReturn("access-token");

        AuthResponse response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.role()).isEqualTo("CITIZEN");
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void register_rejectsDuplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "Ion", "ion@example.com", "password1", "password1", null);
        when(userRepository.existsByEmail("ion@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Exista deja un cont");
    }

    @Test
    void register_rejectsPasswordMismatch() {
        RegisterRequest request = new RegisterRequest(
                "Ion", "ion@example.com", "password1", "password2", null);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nu coincid");
    }

    @Test
    void login_returnsAuthResponseOnSuccess() {
        LoginRequest request = new LoginRequest("ion@example.com", "password1");
        User user = sampleUser();
        Authentication authentication = org.springframework.security.authentication.UsernamePasswordAuthenticationToken
                .authenticated(
                        new org.springframework.security.core.userdetails.User(
                                "ion@example.com", "encoded", java.util.List.of()),
                        null,
                        java.util.List.of());

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateAccessToken(any(), any())).thenReturn("access-token");

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        verify(userRepository).save(user);
    }

    @Test
    void login_throwsWhenUserMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("missing@example.com", "x")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_throwsWhenAccountLocked() {
        User user = sampleUser();
        user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("ion@example.com", "password1")))
                .isInstanceOf(LockedException.class);
    }

    @Test
    void resetPassword_rejectsMismatch() {
        ResetPasswordRequest request = new ResetPasswordRequest("token", "a", "b");

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void logout_revokesTokenWhenPresent() {
        when(refreshTokenRepository.findByTokenHashAndRevokedFalse(anyString()))
                .thenReturn(Optional.empty());

        authService.logout("some-refresh-token");

        verify(refreshTokenRepository, never()).save(any());
    }

    private static User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ion@example.com");
        user.setFullName("Ion Popescu");
        user.setRole(Role.CITIZEN);
        user.setPasswordHash("encoded");
        return user;
    }
}
