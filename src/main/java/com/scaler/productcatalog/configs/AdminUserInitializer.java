package com.scaler.productcatalog.configs;

import com.scaler.productcatalog.repositories.UserRepository;
import com.scaler.productcatalog.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Creates the first ADMIN account from ADMIN_EMAIL / ADMIN_PASSWORD so that write endpoints
 * are usable after a fresh deployment. Public sign-up only ever grants NON_ADMIN_USER.
 */
@Component
public class AdminUserInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }
        authService.registerAdmin(adminEmail, adminPassword);
        log.info("Created bootstrap admin user {}", adminEmail);
    }
}
