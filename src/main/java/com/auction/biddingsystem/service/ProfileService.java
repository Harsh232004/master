package com.auction.biddingsystem.service;

import com.auction.biddingsystem.Repo.ProfileRepository;
import com.auction.biddingsystem.model.Profile;
import com.auction.biddingsystem.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService customerService;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, UserService customerService) {
        this.profileRepository = profileRepository;
        this.customerService = customerService;
    }

    public Optional<Profile> findProfileByCustomerId(Long customerId) {
        return Optional.ofNullable(profileRepository.findByCustomerId(customerId));
    }

    public void saveOrUpdateProfile(Profile profile) {
        Optional<Customer> customerOpt = customerService.findById(profile.getCustomer().getId());
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found for ID: " + profile.getCustomer().getId());
        }

        Customer customer = customerOpt.get();
        profile.setCustomer(customer);

        Profile existingProfile = profileRepository.findByCustomerId(profile.getCustomer().getId());

        if (existingProfile != null) {
            existingProfile.setName(profile.getName());
            existingProfile.setEmail(profile.getEmail());
            existingProfile.setPhoneNumber(profile.getPhoneNumber());
            existingProfile.setBio(profile.getBio());
            existingProfile.setCustomer(customer);

            profileRepository.save(existingProfile);
        } else {
            profileRepository.save(profile);
        }
    }
}
