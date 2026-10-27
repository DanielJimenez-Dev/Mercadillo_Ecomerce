package com.ecommerce.users.service;

import com.ecommerce.shared.security.JwtService;
import com.ecommerce.users.dto.AuthResponse;
import com.ecommerce.users.dto.LoginRequest;
import com.ecommerce.users.dto.RegisterRequest;
import com.ecommerce.users.model.Role;
import com.ecommerce.users.model.User;
import com.ecommerce.users.repository.RoleRepository;
import com.ecommerce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            throw new RuntimeException("El email ya se encuentra registrado.");
        }

        String roleCode = (request.getRole() != null && !request.getRole().isBlank()) 
                ? request.getRole() 
                : "ROLE_BUYER";

        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleCode));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .isActive(true)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(roleCode)
                .message("Usuario registrado exitosamente")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(user);
        String mainRole = user.getRoles().stream()
                .findFirst()
                .map(Role::getCode)
                .orElse("ROLE_BUYER");

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(mainRole)
                .message("Inicio de sesión exitoso")
                .build();
    }
}