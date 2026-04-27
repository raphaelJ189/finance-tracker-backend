package com.financetracker.service;

import com.financetracker.dto.request.LoginRequest;
import com.financetracker.dto.request.RegisterRequest;
import com.financetracker.dto.response.AuthResponse;
import com.financetracker.entity.User;
import com.financetracker.entity.enums.Role;
import com.financetracker.exception.DuplicateResourceException;
import com.financetracker.repository.UserRepository;
import com.financetracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Register a new user
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        log.info("Registration attempt for email: {}", request.getEmail());

        // Check if email is already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "User", "email", request.getEmail()
            );
        }

        // Build and save the new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .currency(request.getCurrency())
                .role(Role.USER)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        log.info("User registered successfully: id={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        // Generate JWT token immediately
        // User is logged in right after registration
        String token = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return buildAuthResponse(savedUser, token);
    }

    // Login existing user
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        log.info("Login attempt for email: {}", request.getEmail());

        // AuthenticationManager handles:
        // Finding user by email
        // BCrypt password verification
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        log.info("Login successful: id={}, email={}",
                user.getId(), user.getEmail());

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return buildAuthResponse(user, token);
    }

    // Private helper
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .currency(user.getCurrency())
                .build();
    }
}