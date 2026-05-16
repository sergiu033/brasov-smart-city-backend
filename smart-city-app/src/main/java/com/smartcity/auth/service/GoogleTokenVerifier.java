package com.smartcity.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.smartcity.auth.dto.GoogleUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;
    private final boolean enabled;

    public GoogleTokenVerifier(@Value("${app.oauth.google.client-id:}") String clientId) {
        this.enabled = StringUtils.hasText(clientId);
        this.verifier = enabled
                ? new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                        .setAudience(Collections.singletonList(clientId))
                        .build()
                : null;
    }

    public GoogleUserInfo verify(String idTokenString) {
        if (!enabled) {
            throw new IllegalStateException("Autentificarea Google nu este configurata pe server.");
        }
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new BadCredentialsException("Token Google invalid sau expirat.");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                throw new BadCredentialsException("Email-ul Google nu este verificat.");
            }

            String email = payload.getEmail();
            if (!StringUtils.hasText(email)) {
                throw new BadCredentialsException("Email-ul Google lipseste.");
            }

            String name = payload.get("name") instanceof String value ? value : email;
            return new GoogleUserInfo(payload.getSubject(), email, name);
        } catch (GeneralSecurityException | IOException ex) {
            throw new BadCredentialsException("Nu s-a putut valida token-ul Google.", ex);
        }
    }
}
