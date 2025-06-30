package com.auction.biddingsystem.controller;

import com.auction.biddingsystem.model.Customer;
import com.auction.biddingsystem.model.Product;
import com.auction.biddingsystem.service.ProductService;
import com.auction.biddingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/harsh")
public class AuctionController {

    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public AuctionController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/MainAuction")
    public String mainAuctionPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            userService.findCustomerByUsername(username).ifPresent(value -> model.addAttribute("customer", value));
        }
        List<Product> products = productService.findAllProducts();
        model.addAttribute("products", products);
        return "MainAuction";
    }

    @PostMapping("/bid")    
    @PreAuthorize("hasRole('ROLE_USER')")
    public String placeBid(@RequestParam("productId") Long productId,
                           @RequestParam("bidAmount") BigDecimal bidAmount,
                           Principal principal) {
        Optional<Customer> bidder = userService.findCustomerByUsername(principal.getName());
        Optional<Product> product = productService.getProductById(productId);

        if (bidder.isEmpty() || product.isEmpty()) {
            return "redirect:/errorPage";
        }

        try {
            productService.placeBid(productId, bidder.get(), bidAmount);
        } catch (Exception e) {
            return "redirect:/errorPage";
        }

        return "redirect:/harsh/MainAuction";
    }

    @GetMapping("/createAuction")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showCreateAuctionForm(Model model) {
        model.addAttribute("product", new Product());
        return "createAuctionForm";
    }

    @PostMapping("/createAuction")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createAuction(@ModelAttribute @Valid Product product, BindingResult result) {
        if (result.hasErrors()) {
            return "createAuctionForm";
        }

        try {
            productService.saveProduct(product);
        } catch (Exception e) {
            return "redirect:/errorPage";
        }

        return "redirect:/harsh/MainAuction";
    }
}
