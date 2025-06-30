package com.auction.biddingsystem.controller;

import com.auction.biddingsystem.model.Profile;
import com.auction.biddingsystem.model.Customer;
import com.auction.biddingsystem.service.ProfileService;
import com.auction.biddingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {

    private final ProfileService profileService;
    private final UserService customerService;

    @Autowired
    public ProfileController(ProfileService profileService, UserService customerService) {
        this.profileService = profileService;
        this.customerService = customerService;
    }


    @GetMapping("/customer/{customerId}/profile/view")
    public String viewProfile(@PathVariable Long customerId, Model model) {

        Profile profile = profileService.findProfileByCustomerId(customerId)
                .orElse(null);

        if (profile == null) {

            return "redirect:/customer/" + customerId + "/profile/edit";
        }


        model.addAttribute("profile", profile);
        model.addAttribute("editable", false);
        model.addAttribute("customerId", customerId);


        return "profileForm";
    }


    @GetMapping("/customer/{customerId}/profile/edit")
    public String editProfile(@PathVariable Long customerId, Model model) {

        Profile profile = profileService.findProfileByCustomerId(customerId)
                .orElse(new Profile());

        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found for ID: " + customerId));
        profile.setCustomer(customer);


        model.addAttribute("profile", profile);
        model.addAttribute("editable", true);
        model.addAttribute("customerId", customerId);


        return "profileForm";
    }


    @PostMapping("/customer/{customerId}/profile/save")
    public String saveProfile(@PathVariable Long customerId, @ModelAttribute Profile profile) {

        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found for ID: " + customerId));


        profile.setCustomer(customer);


        profileService.saveOrUpdateProfile(profile);


        return "redirect:/customer/" + customerId + "/profile/view";
    }
}
