package com.smartcity.user.controller;

import com.smartcity.user.dto.UserProfileResponse;
import com.smartcity.user.service.UserService;
import com.smartcity.user_vehicles.dto.VehicleRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void profile_returnsCurrentUserProfile() {
        Authentication authentication = authenticatedUser("ion@example.com");
        UserProfileResponse profile = UserProfileResponse.builder()
                .id(1L)
                .fullName("Ion Popescu")
                .email("ion@example.com")
                .role("CITIZEN")
                .vehicles(Set.of())
                .build();

        when(userService.getCurrentUserProfile("ion@example.com")).thenReturn(profile);

        ResponseEntity<UserProfileResponse> response = userController.profile(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(profile);
        verify(userService).getCurrentUserProfile("ion@example.com");
    }

    @Test
    void uploadProfilePicture_returnsUpdatedProfile() {
        Authentication authentication = authenticatedUser("ion@example.com");
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3});
        UserProfileResponse profile = UserProfileResponse.builder()
                .id(1L)
                .fullName("Ion Popescu")
                .email("ion@example.com")
                .role("CITIZEN")
                .profilePictureUrl("2026/0517/avatar.jpg")
                .vehicles(Set.of())
                .build();

        when(userService.updateProfilePicture("ion@example.com", image)).thenReturn(profile);

        ResponseEntity<UserProfileResponse> response =
                userController.uploadProfilePicture(image, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(profile);
        verify(userService).updateProfilePicture("ion@example.com", image);
    }

    @Test
    void addVehicle_returnsUpdatedProfile() {
        Authentication authentication = authenticatedUser("ion@example.com");
        VehicleRequest request = new VehicleRequest("BV 01 ABC");
        UserProfileResponse profile = UserProfileResponse.builder()
                .id(1L)
                .fullName("Ion Popescu")
                .email("ion@example.com")
                .role("CITIZEN")
                .vehicles(Set.of())
                .build();

        when(userService.addVehicle(request, authentication)).thenReturn(profile);

        ResponseEntity<UserProfileResponse> response = userController.addVehicle(request, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(profile);
        verify(userService).addVehicle(request, authentication);
    }

    @Test
    void vehicleRequest_rejectsBlankPlateNumber() {
        Set<ConstraintViolation<VehicleRequest>> violations =
                validator.validate(new VehicleRequest(""));

        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isNotBlank();
    }

    private static Authentication authenticatedUser(String email) {
        return UsernamePasswordAuthenticationToken.authenticated(email, null, List.of());
    }
}
