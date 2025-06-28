package com.auction.biddingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Authorization configuration
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**", "/WelcomePage", "/register", "/perform_login").permitAll() // Publicly accessible URLs
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Restrict /admin URLs to ADMIN role
                        .requestMatchers("/harsh/**").authenticated() // Require authentication for auction-related pages
                        .anyRequest().authenticated() // Authenticate all other requests
                )
                // Login configuration
                .formLogin(login -> login
                        .loginPage("/WelcomePage") // Custom login page
                        .loginProcessingUrl("/perform_login") // URL for form login processing
                        .defaultSuccessUrl("/harsh/MainAuction", true) // Redirect on successful login
                        .failureUrl("/WelcomePage?error=true") // Redirect on login failure
                        .permitAll()
                )
                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/perform_logout") // URL for logout
                        .logoutSuccessUrl("/WelcomePage") // Redirect after logout
                        .invalidateHttpSession(true) // Invalidate session
                        .clearAuthentication(true) // Clear authentication
                        .permitAll()
                )
                // Custom UserDetailsService for user authentication
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
