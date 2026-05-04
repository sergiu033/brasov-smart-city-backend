package com.smartcity.auth.service;

import com.smartcity.user.entity.Role;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("Utilizatorul nu a fost gasit."));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                isAccountNonLocked(user),
                true,
                true,
                true,
                mapAuthorities(user.getRole()));
    }

    private boolean isAccountNonLocked(User user) {
        return user.getLockedUntil() == null || user.getLockedUntil().isBefore(java.time.LocalDateTime.now());
    }

    private Collection<? extends GrantedAuthority> mapAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
