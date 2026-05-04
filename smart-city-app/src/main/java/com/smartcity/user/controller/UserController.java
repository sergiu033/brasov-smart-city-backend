package com.smartcity.user.controller;

import com.smartcity.common.api.ApiResponse;
import com.smartcity.user.dto.UserProfileResponse;
import com.smartcity.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> me(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.getCurrentUserProfile(authentication.getName()),
                "Profil incarcat."));
    }
}
