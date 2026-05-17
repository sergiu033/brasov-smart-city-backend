package com.smartcity.auth.service;

import com.smartcity.user.entity.Role;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityUserDetailsService securityUserDetailsService;

    @Test
    void loadUserByUsername_returnsUserDetails() {
        User user = new User();
        user.setEmail("ion@example.com");
        user.setPasswordHash("encoded");
        user.setRole(Role.CITIZEN);

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));

        UserDetails details = securityUserDetailsService.loadUserByUsername("  ION@Example.COM  ");

        assertThat(details.getUsername()).isEqualTo("ion@example.com");
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_CITIZEN");
        assertThat(details.isAccountNonLocked()).isTrue();
    }

    @Test
    void loadUserByUsername_marksLockedAccount() {
        User user = new User();
        user.setEmail("ion@example.com");
        user.setPasswordHash("encoded");
        user.setRole(Role.CITIZEN);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(10));

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));

        UserDetails details = securityUserDetailsService.loadUserByUsername("ion@example.com");

        assertThat(details.isEnabled()).isFalse();
    }

    @Test
    void loadUserByUsername_throwsWhenMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> securityUserDetailsService.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
