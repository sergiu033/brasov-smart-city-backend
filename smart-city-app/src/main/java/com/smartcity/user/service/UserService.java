package com.smartcity.user.service;

import com.smartcity.imagestorage.service.ImageService;
import com.smartcity.user.dto.UserProfileResponse;
import com.smartcity.user.entity.User;
import com.smartcity.user.mapper.UserMapper;
import com.smartcity.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/png", "image/jpeg", "image/jpg");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg");

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ImageService imageService;

    public UserProfileResponse getCurrentUserProfile(String email) {
        return userMapper.toDto(findUserByEmail(email));
    }

    @Transactional
    public UserProfileResponse updateProfilePicture(String email, MultipartFile image) {
        validateProfileImage(image);

        User user = findUserByEmail(email);
        try {
            String storedPath = imageService.saveImage(
                    image.getInputStream(),
                    image.getOriginalFilename());
            user.setProfilePictureUrl(storedPath);
        } catch (IOException e) {
            throw new IllegalStateException("Nu s-a putut salva imaginea de profil.");
        }

        return userMapper.toDto(userRepository.save(user));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new EntityNotFoundException("Utilizatorul nu a fost gasit."));
    }

    private void validateProfileImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Imaginea este obligatorie.");
        }

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Format invalid. Sunt acceptate doar PNG si JPEG.");
        }

        String extension = getFileExtension(image.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Format invalid. Sunt acceptate doar PNG si JPEG.");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return fileName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }
}
