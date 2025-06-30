package com.auction.biddingsystem.controller;

import com.auction.biddingsystem.Repo.CustomerRepository;
import com.auction.biddingsystem.model.Bid;
import com.auction.biddingsystem.model.Customer;
import com.auction.biddingsystem.model.Product;
import com.auction.biddingsystem.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final ProductService productService;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, ProductService productService) {
        this.customerRepository = customerRepository;
        this.productService = productService;
    }

    @GetMapping("/auction")
    public String viewAuctions(Model model) {
        model.addAttribute("auctions", productService.findAllProducts());
        return "auctionList";
    }

    @PostMapping("/placeBid")
    public ResponseEntity<?> placeBid(@RequestParam Long productId, @RequestParam BigDecimal bidAmount, Principal principal) {
        try {
            String username = principal.getName();
            Customer customer = customerRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Bid bid = productService.placeBid(productId, customer, bidAmount);
            return ResponseEntity.ok(bid);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bids")
    public String viewCustomerBids(Model model, Principal principal) {
        String username = principal.getName();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        model.addAttribute("bids", productService.getBidsByCustomer(customer));
        return "bidHistory";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        // If getProductById returns Optional<Product>, use this
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping("/save")
    public ResponseEntity<Product> saveProduct(@Valid @RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping("/auction/{productId}/bids")
    public ResponseEntity<List<Bid>> getBidsForProduct(@PathVariable Long productId) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Bid> bids = productService.getBidsForProduct(product);
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/about")
    public String aboutUs() {
        return "About_us";
    }
}
