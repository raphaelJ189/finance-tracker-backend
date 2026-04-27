package com.financetracker.controller.v1;

import com.financetracker.dto.response.UserResponse;
import com.financetracker.entity.User;
import com.financetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // Get current user's own profile
    // Any authenticated user can access this
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal User currentUser) {

        UserResponse response = userService.getMyProfile(currentUser);
        return ResponseEntity.ok(response);
    }

    // Admin endpoints — only ADMIN role
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/users/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable Long id) {

        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/users/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(
            @PathVariable Long id) {

        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }
}