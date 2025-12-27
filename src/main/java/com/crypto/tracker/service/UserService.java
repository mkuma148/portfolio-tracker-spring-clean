package com.crypto.tracker.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.crypto.tracker.model.User;
import com.crypto.tracker.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long signup(String username, String rawPassword, String email) {
        if (userRepository.findByName(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        return userRepository.save(user).getId();
    }

    public boolean login(String username, String rawPassword) {
        Optional<User> user = userRepository.findByName(username);
        if (user == null) return false;
        return passwordEncoder.matches(rawPassword, user.get().getPassword());
    }
}