package com.findataagg.alertconfigservice.users.service;

import com.findataagg.alertconfigservice.users.dto.UserRegistrationRequest;
import com.findataagg.alertconfigservice.users.repository.UserRepository;
import com.findataagg.common.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());

        // Hash the password before saving
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        return userRepository.save(user);
    }
}
