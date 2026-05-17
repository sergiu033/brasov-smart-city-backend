package com.smartcity.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET =
            "ZkFrZUJhc2U2NEtleUZvclNtYXJ0Q2l0eUFwcEJhY2tlbmQxMjM0NTY3ODkwMTIzNDU2Nzg=";

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 900_000);
        userDetails = new User(
                "ion@example.com",
                "hash",
                List.of(new SimpleGrantedAuthority("ROLE_CITIZEN")));
    }

    @Test
    void generateAndValidateToken() {
        String token = jwtService.generateAccessToken(
                userDetails, Map.of("role", "CITIZEN", "name", "Ion Popescu"));

        assertThat(jwtService.extractUsername(token)).isEqualTo("ion@example.com");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalseForWrongUser() {
        String token = jwtService.generateAccessToken(userDetails, Map.of());
        UserDetails otherUser = new User(
                "other@example.com",
                "hash",
                List.of(new SimpleGrantedAuthority("ROLE_CITIZEN")));

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }
}
