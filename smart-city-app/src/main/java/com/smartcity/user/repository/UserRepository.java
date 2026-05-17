package com.smartcity.user.repository;

import com.smartcity.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query("""
        SELECT u
        FROM User u
        LEFT JOIN FETCH u.vehicles
        WHERE u.email = :email
    """)
    Optional<User> findByEmail(@Param("email") String email);

    Optional<User> findByGoogleId(String googleId);
}
