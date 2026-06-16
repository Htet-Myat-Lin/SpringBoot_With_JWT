package com.example.app.service;

import com.example.app.dto.request.UserRegisterRequest;
import com.example.app.exception.DuplicateResourceException;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(UserRegisterRequest req) throws Exception {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("An account already exists with this email");
        }

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        return userRepository.save(user);
    }
}
