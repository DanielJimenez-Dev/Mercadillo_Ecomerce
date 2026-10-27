package com.ecommerce.shared.security;

import com.ecommerce.users.model.Role;
import com.ecommerce.users.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.findByCode("ROLE_BUYER").isEmpty()) {
            roleRepository.save(Role.builder().code("ROLE_BUYER").build());
        }
        if (roleRepository.findByCode("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(Role.builder().code("ROLE_ADMIN").build());
        }
    }
}