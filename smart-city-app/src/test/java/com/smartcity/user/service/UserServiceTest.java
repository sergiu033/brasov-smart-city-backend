package com.smartcity.user.service;

import com.smartcity.imagestorage.service.ImageService;
import com.smartcity.user.dto.UserProfileResponse;
import com.smartcity.user.entity.Role;
import com.smartcity.user.entity.User;
import com.smartcity.user.mapper.UserMapper;
import com.smartcity.user.repository.UserRepository;
import com.smartcity.user_vehicles.dto.VehicleRequest;
import com.smartcity.user_vehicles.entity.Vehicle;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserService userService;

    @Test
    void getCurrentUserProfile_returnsMappedProfile() {
        User user = sampleUser();
        UserProfileResponse expected = sampleProfileResponse();

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        UserProfileResponse result = userService.getCurrentUserProfile("  ION@Example.COM  ");

        assertThat(result).isEqualTo(expected);
        verify(userRepository).findByEmail("ion@example.com");
    }

    @Test
    void getCurrentUserProfile_throwsWhenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUserProfile("missing@example.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Utilizatorul nu a fost gasit.");
    }

    @Test
    void updateProfilePicture_savesImageAndUpdatesUser() throws IOException {
        User user = sampleUser();
        user.setVehicles(new LinkedHashSet<>());
        MultipartFile image = mockImage("photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
        User savedUser = sampleUser();
        savedUser.setProfilePictureUrl("2026/0517/uuid.jpg");
        UserProfileResponse expected = sampleProfileResponse();

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(imageService.saveImage(any(InputStream.class), eq("photo.jpg"))).thenReturn("2026/0517/uuid.jpg");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expected);

        UserProfileResponse result = userService.updateProfilePicture("ion@example.com", image);

        assertThat(result).isEqualTo(expected);
        assertThat(user.getProfilePictureUrl()).isEqualTo("2026/0517/uuid.jpg");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfilePicture_rejectsMissingImage() {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfilePicture("ion@example.com", image))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Imaginea este obligatorie.");
    }

    @Test
    void updateProfilePicture_rejectsInvalidContentType() {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/gif");

        assertThatThrownBy(() -> userService.updateProfilePicture("ion@example.com", image))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Format invalid. Sunt acceptate doar PNG si JPEG.");
    }

    @Test
    void updateProfilePicture_rejectsInvalidExtension() {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(image.getOriginalFilename()).thenReturn("photo.gif");
        when(image.getContentType()).thenReturn("image/jpeg");

        assertThatThrownBy(() -> userService.updateProfilePicture("ion@example.com", image))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Format invalid. Sunt acceptate doar PNG si JPEG.");
    }

    @Test
    void updateProfilePicture_wrapsIOExceptionFromImageService() throws IOException {
        User user = sampleUser();
        MultipartFile image = mockImage("photo.jpg", "image/jpeg", new byte[]{1});

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(imageService.saveImage(any(InputStream.class), eq("photo.jpg")))
                .thenThrow(new IOException("disk full"));

        assertThatThrownBy(() -> userService.updateProfilePicture("ion@example.com", image))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Nu s-a putut salva imaginea de profil.");
    }

    @Test
    void addVehicle_attachesVehicleToUserAndReturnsProfile() {
        User user = sampleUser();
        user.setVehicles(new LinkedHashSet<>());
        Authentication authentication = mock(Authentication.class);
        VehicleRequest request = new VehicleRequest("BV 99 XYZ");
        UserProfileResponse expected = sampleProfileResponse();

        when(authentication.getName()).thenReturn("ion@example.com");
        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserProfileResponse result = userService.addVehicle(request, authentication);

        assertThat(result).isEqualTo(expected);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertThat(userCaptor.getValue().getVehicles()).hasSize(1);
        Vehicle addedVehicle = userCaptor.getValue().getVehicles().iterator().next();
        assertThat(addedVehicle.getPlateNumber()).isEqualTo("BV 99 XYZ");
        assertThat(addedVehicle.getUser()).isSameAs(user);
    }

    private static User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Ion Popescu");
        user.setEmail("ion@example.com");
        user.setRole(Role.CITIZEN);
        return user;
    }

    private static UserProfileResponse sampleProfileResponse() {
        return UserProfileResponse.builder()
                .id(1L)
                .fullName("Ion Popescu")
                .email("ion@example.com")
                .role("CITIZEN")
                .build();
    }

    private static MultipartFile mockImage(String fileName, String contentType, byte[] content) {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(image.getOriginalFilename()).thenReturn(fileName);
        when(image.getContentType()).thenReturn(contentType);
        try {
            when(image.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return image;
    }
}
