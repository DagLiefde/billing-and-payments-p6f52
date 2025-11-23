package com.fabrica.p6f5.springapp.config;

import com.fabrica.p6f5.springapp.entity.User;
import com.fabrica.p6f5.springapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data loader for initial data setup.
 * Creates default admin user if it doesn't exist.
 */
@Component
public class DataLoader implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) throws Exception {
        createAdminUser();
    }
    
    /**
     * Create admin user if it doesn't exist.
     */
    private void createAdminUser() {
        if (userRepository.findByUsername("admin").isPresent() || 
            userRepository.existsByEmail("admin@example.com")) {
            logger.info("Admin user already exists, skipping creation");
            return;
        }
        
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("Admin123!"));
        admin.setRole("ADMIN");
        admin.setFullName("Administrator");
        admin.setIsActive(true);
        
        userRepository.save(admin);
        logger.info("Admin user created successfully");
        logger.info("Username: admin");
        logger.info("Email: admin@example.com");
        logger.info("Password: Admin123!");
    }
}






