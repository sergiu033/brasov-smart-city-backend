package com.smartcity.auth.service;

import com.smartcity.auth.dto.*;
import com.smartcity.auth.entity.RefreshToken;
import com.smartcity.auth.repository.RefreshTokenRepository;
import com.smartcity.security.JwtService;
import com.smartcity.user.entity.Role;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 30;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final long refreshTokenExpirationDays;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            GoogleTokenVerifier googleTokenVerifier,
            @Value("${app.security.jwt.refresh-token-expiration-days}") long refreshTokenExpirationDays) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.googleTokenVerifier = googleTokenVerifier;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Exista deja un cont cu acest email.");
        }
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Parolele introduse nu coincid.");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role() == null ? Role.CITIZEN : request.role());
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Email sau parola incorecta."));

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new LockedException("Contul tau este blocat. Incearca dupa 30 minute.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password()));
            resetFailedAttempts(user);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            return buildAuthResponse(user, (UserDetails) authentication.getPrincipal());
        } catch (BadCredentialsException ex) {
            registerFailedAttempt(user);
            throw new BadCredentialsException("Email sau parola incorecta.");
        }
    }

    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleUserInfo googleUser = googleTokenVerifier.verify(request.idToken());
        String email = normalizeEmail(googleUser.email());

        User user = userRepository.findByGoogleId(googleUser.googleId())
                .or(() -> userRepository.findByEmail(email))
                .orElseGet(() -> createGoogleUser(googleUser, email));

        if (user.getGoogleId() != null && !user.getGoogleId().equals(googleUser.googleId())) {
            throw new IllegalArgumentException("Acest email este asociat altui cont Google.");
        }

        if (user.getGoogleId() == null) {
            user.setGoogleId(googleUser.googleId());
        }

        if (!StringUtils.hasText(user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        }

        resetFailedAttempts(user);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String hash = hashToken(request.refreshToken());
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(hash)
                .orElseThrow(() -> new EntityNotFoundException("Refresh token invalid sau expirat."));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new IllegalArgumentException("Refresh token expirat.");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return buildAuthResponse(refreshToken.getUser());
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        normalizeEmail(request.email());
    }

    public void resetPassword(ResetPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Parolele introduse nu coincid.");
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
        return buildAuthResponse(user, userDetails);
    }

    private AuthResponse buildAuthResponse(User user, UserDetails userDetails) {
        String accessToken = jwtService.generateAccessToken(userDetails, Map.of(
                "role", user.getRole().name(),
                "name", user.getFullName()));
        String refreshTokenValue = UUID.randomUUID().toString() + UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(refreshTokenValue));
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshTokenExpirationDays));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshTokenValue,
                "Bearer",
                user.getRole().name(),
                user.getFullName());
    }

    private void registerFailedAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
            user.setFailedLoginAttempts(0);
        }
        userRepository.save(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
    }

    private User createGoogleUser(GoogleUserInfo googleUser, String email) {
        User user = new User();
        user.setFullName(googleUser.fullName().trim());
        user.setEmail(email);
        user.setGoogleId(googleUser.googleId());
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setRole(Role.CITIZEN);
        return userRepository.save(user);
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().trim();
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Nu s-a putut genera hash-ul pentru token.", ex);
        }
    }
}
