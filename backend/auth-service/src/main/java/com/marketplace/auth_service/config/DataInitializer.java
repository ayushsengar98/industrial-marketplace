package com.marketplace.auth_service.config;

import com.marketplace.auth_service.model.Role;
import com.marketplace.auth_service.model.User;
import com.marketplace.auth_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {

            if (repo.findByEmail("admin@marketplace.com").isEmpty()) {

                User admin = new User();
                admin.setEmail("admin@marketplace.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);

                repo.save(admin);

                System.out.println("✅ Default Admin Created");
                System.out.println("Email: admin@marketplace.com");
                System.out.println("Password: admin123");
            }
        };
    }
}
