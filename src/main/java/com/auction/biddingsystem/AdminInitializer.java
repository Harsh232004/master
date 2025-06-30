package com.auction.biddingsystem;

import com.auction.biddingsystem.Repo.AdminRepository;
import com.auction.biddingsystem.model.Admin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminInitializer(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Check if admin already exists
        adminRepository.findByUsername("admin").ifPresentOrElse(admin -> {
            String newRawPassword = "2004"; // Replace with the desired new password
            admin.setPassword(passwordEncoder.encode(newRawPassword));
            admin.setRole("admin"); // Set the role
            adminRepository.save(admin);
            System.out.println("Admin password updated.");
        }, () -> {
            // Create a new admin if one does not exist
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminPassword"));// Set the role
            adminRepository.save(admin);
            System.out.println("Admin account created.");
        });
    }
}
