package com.smartcity.auth.dto;

public record GoogleUserInfo(
        String googleId,
        String email,
        String fullName) {
}
