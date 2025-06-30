package com.auction.biddingsystem.controller;

import com.auction.biddingsystem.model.Product;
import com.auction.biddingsystem.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/auctionForm")
    public String showAuctionForm(@RequestParam(value = "id", required = false) Long id, Model model) {
        if (id != null) {
            Optional<Product> product = productService.getProductById(id);
            logger.warn("Product with ID {} not found", id);
            model.addAttribute("errorMessage", "Product not found.");
            return "redirect:/harsh/MainAuction?error=notfound";
        } else {
            model.addAttribute("isEditMode", false);
            model.addAttribute("product", new Product());
        }
        return "Create";
    }

    @PostMapping("/auctionForm")
    public String saveAuction(@ModelAttribute("product") Product product,
                              BindingResult result,
                              Model model,
                              @RequestParam(value = "isEditMode", defaultValue = "false") boolean isEditMode) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "There were errors in the form. Please correct them and try again.");
            logger.error("Form submission contains errors: {}", result.getAllErrors());
            model.addAttribute("isEditMode", isEditMode);
            return "auctionForm";
        }

        try {
            setDefaultAuctionTimes(product);
            validateAndSetStartingPrice(product);

            if (isEditMode) {
                productService.updateProduct(product);
                logger.info("Auction product updated successfully: {}", product);
            } else {
                productService.saveProduct(product);
                logger.info("Auction product created successfully: {}", product);
            }
        } catch (Exception e) {
            logger.error("Error occurred while saving the auction product", e);
            model.addAttribute("errorMessage", "An error occurred while saving the auction item. Please try again.");
            model.addAttribute("isEditMode", isEditMode);
            return "auctionForm";
        }

        return "redirect:/harsh/MainAuction?success=true";
    }

    @PostMapping("/deleteAuction/{id}")
    public String deleteAuction(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/harsh/MainAuction?deleted=true";
    }

    private void setDefaultAuctionTimes(Product product) {
        if (product.getAuctionStartTime() == null) {
            product.setAuctionStartTime(LocalDateTime.now());
        }
        if (product.getAuctionEndTime() == null || product.getAuctionEndTime().isBefore(product.getAuctionStartTime())) {
            product.setAuctionEndTime(product.getAuctionStartTime().plusDays(1));
            logger.warn("Auction end time not provided or invalid. Set to default 1 day after start.");
        }
    }

    private void validateAndSetStartingPrice(Product product) {
        if (product.getStartingPrice() == null || product.getStartingPrice().compareTo(BigDecimal.ZERO) < 0) {
            product.setStartingPrice(BigDecimal.ZERO);
            logger.warn("Invalid starting price provided. Defaulted to 0.");
        }
    }
}
