package com.ecommerce.users.controller;

import com.ecommerce.users.dto.UserResponse;
import com.ecommerce.users.model.Role;
import com.ecommerce.users.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getAuthenticatedUser(@AuthenticationPrincipal User user) {
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream()
                        .map(Role::getCode)
                        .collect(Collectors.toSet()))
                .build();

        return ResponseEntity.ok(response);
    }
}
