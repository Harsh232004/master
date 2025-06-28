package com.auction.biddingsystem.service;

import com.auction.biddingsystem.model.Customer;
import com.auction.biddingsystem.Repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Optional<Customer> findCustomerByUsername(String username) {
        return customerRepository.findByUsername(username);
    }


    public void saveUser(Customer customer) {
        if (customer.getPassword() != null) {

            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }
        customerRepository.save(customer);
    }


    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

}
