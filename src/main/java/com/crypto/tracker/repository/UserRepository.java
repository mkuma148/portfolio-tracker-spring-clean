package com.crypto.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crypto.tracker.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
