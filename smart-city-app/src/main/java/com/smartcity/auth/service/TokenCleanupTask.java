package com.smartcity.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupTask {

    private final AuthService authService;

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired and revoked refresh tokens...");
        authService.cleanupExpiredTokens();
        log.info("Cleanup of expired and revoked refresh tokens completed.");
    }
}
