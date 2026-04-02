package com.sliit.studentplatform.config;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Ensures at least one user exists in the database for fallback purposes.
 * This is particularly useful when authentication is disabled.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        ensureDefaultUserExists();
    }

    private void ensureDefaultUserExists() {
        String defaultEmail = "admin@sliit.com";
        Optional<User> existingUser = userRepository.findByEmail(defaultEmail);
        
        if (existingUser.isEmpty()) {
            log.info("No default user found. Creating initial admin user...");
            User admin = User.builder()
                    .fullName("Default Admin")
                    .email(defaultEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Initial admin user created with email: {}", defaultEmail);
        } else {
            log.info("Default user already exists: {}", defaultEmail);
        }
    }
}
