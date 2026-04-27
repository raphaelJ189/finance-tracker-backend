package com.financetracker.service;

import com.financetracker.dto.response.UserResponse;
import com.financetracker.entity.User;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.mapper.UserMapper;
import com.financetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // Get current user profile
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(User currentUser) {
        log.debug("Fetching profile for userId: {}", currentUser.getId());
        return userMapper.toResponse(currentUser);
    }

    // Get all users admin only
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Admin fetching all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get user by id: admin only
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Admin fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponse(user);
    }

    // Deactivate a user account — admin only
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Admin deactivating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (!user.isActive()) {
            log.warn("User {} is already deactivated", id);
            return;
        }

        user.setActive(false);
        userRepository.save(user);

        log.info("User deactivated successfully: {}", id);
    }

    // Reactivate a user account: admin only
    @Transactional
    public void activateUser(Long id) {
        log.info("Admin activating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (user.isActive()) {
            log.warn("User {} is already active", id);
            return;
        }

        user.setActive(true);
        userRepository.save(user);
        log.info("User activated successfully: {}", id);
    }
}