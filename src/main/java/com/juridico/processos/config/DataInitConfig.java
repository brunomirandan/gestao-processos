package com.juridico.processos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.juridico.processos.model.Role;
import com.juridico.processos.model.UserAccount;
import com.juridico.processos.repository.RoleRepository;
import com.juridico.processos.repository.UserAccountRepository;

@Configuration
public class DataInitConfig {

    @Bean
    CommandLineRunner initUsers(UserAccountRepository userRepo,
                                RoleRepository roleRepo,
                                PasswordEncoder encoder) {
        return args -> {
            Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADMIN")));
            Role userRole = roleRepo.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_USER")));

            if (userRepo.findByUsername("admin").isEmpty()) {
                UserAccount admin = new UserAccount();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.getRoles().add(adminRole);
                admin.getRoles().add(userRole);
                userRepo.save(admin);
            }
        };
    }
}
