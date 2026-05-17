package com.smartcity.user.controller;

import com.smartcity.user.dto.UserProfileResponse;
import com.smartcity.user_vehicles.dto.VehicleRequest;
import com.smartcity.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> profile(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUserProfile(authentication.getName()));
    }

    @PostMapping(value = "/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> uploadProfilePicture(
            @RequestPart("image") MultipartFile image,
            Authentication authentication) {
        return ResponseEntity.ok(userService.updateProfilePicture(authentication.getName(), image));
    }

    @PutMapping("/vehicles")
    public ResponseEntity<UserProfileResponse> addVehicle(
            @Valid @RequestBody VehicleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok().body(userService.addVehicle(request, authentication));
    }

}
