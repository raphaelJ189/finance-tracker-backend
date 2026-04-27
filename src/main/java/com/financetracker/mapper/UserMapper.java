package com.financetracker.mapper;

import com.financetracker.dto.response.UserResponse;
import com.financetracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Convert User entity to UserResponse DTO
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .currency(user.getCurrency())
                .profilePicture(user.getProfilePicture())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}