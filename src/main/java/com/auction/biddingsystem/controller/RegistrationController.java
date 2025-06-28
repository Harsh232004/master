package com.auction.biddingsystem.controller;

import com.auction.biddingsystem.model.Customer;
import com.auction.biddingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

        private final UserService userService;

        @Autowired
        public RegistrationController(UserService userService) {

                this.userService = userService;
        }

        @GetMapping("/register")
        public String showRegistrationForm(Model model) {
                model.addAttribute("customer", new Customer());
                return "register";
        }

        @PostMapping("/register")
        public String registerUser(@ModelAttribute Customer customer) {
                userService.saveUser(customer);
                return "redirect:/WelcomePage";
        }
}