package com.auction.biddingsystem.service;

import com.auction.biddingsystem.Repo.AdminRepository;
import com.auction.biddingsystem.Repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public UserDetailsServiceImpl(AdminRepository adminRepository, CustomerRepository customerRepository) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDetails adminDetails = loadAdminByUsername(username);
        if (adminDetails != null) {
            return adminDetails;
        }


        UserDetails customerDetails = loadCustomerByUsername(username);
        if (customerDetails != null) {
            return customerDetails;
        }


        throw new UsernameNotFoundException("User not found: " + username);
    }

    private UserDetails loadAdminByUsername(String username) {
        return adminRepository.findByUsername(username)
                .map(admin -> org.springframework.security.core.userdetails.User.withUsername(admin.getUsername())
                        .password(admin.getPassword())
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .build())
                .orElse(null);
    }

    private UserDetails loadCustomerByUsername(String username) {
        return customerRepository.findByUsername(username)
                .map(customer -> org.springframework.security.core.userdetails.User.withUsername(customer.getUsername())
                        .password(customer.getPassword())
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                        .build())
                .orElse(null);
    }
}
