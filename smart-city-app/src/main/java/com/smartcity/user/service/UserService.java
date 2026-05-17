package com.smartcity.user.service;

import com.smartcity.user.dto.UserProfileResponse;
import com.smartcity.user.entity.User;
import com.smartcity.user.mapper.UserMapper;
import com.smartcity.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserProfileResponse getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new EntityNotFoundException("Utilizatorul nu a fost gasit."));

        return userMapper.toDto(user);
    }
}
