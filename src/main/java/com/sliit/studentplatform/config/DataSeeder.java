package com.sliit.studentplatform.config;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.enums.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if admin already exists so we don't create it twice
            if (userRepository.findByEmail("admin@sliit.lk").isEmpty()) {

                User admin = new User();
                admin.setEmail("admin@sliit.lk");
                admin.setFullName("System Admin");

                // FIXED: Using your Role enum instead of a String
                admin.setRole(Role.ADMIN);

                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setEnabled(true);

                userRepository.save(admin);
                System.out.println("✅ Admin account created: admin@sliit.lk / Admin@123");
            }
        };
    }
}