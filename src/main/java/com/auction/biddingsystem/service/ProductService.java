package com.auction.biddingsystem.service;

import com.auction.biddingsystem.Repo.BidRepository;
import com.auction.biddingsystem.Repo.ProductRepository;
import com.auction.biddingsystem.model.Bid;
import com.auction.biddingsystem.model.Customer;
import com.auction.biddingsystem.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BidRepository bidRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    public ProductService(ProductRepository productRepository, BidRepository bidRepository) {
        this.productRepository = productRepository;
        this.bidRepository = bidRepository;
    }


    public Product saveProduct(Product product) {
        logger.info("Saving new auction product: {}", product);
        return productRepository.save(product);
    }


    public Product updateProduct(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new RuntimeException("Product with ID " + product.getId() + " not found.");
        }
        logger.info("Updating existing auction product: {}", product);
        return productRepository.save(product);
    }


    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }



    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }


    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product with ID " + id + " not found.");
        }
        logger.info("Deleting auction product with ID: {}", id);
        productRepository.deleteById(id);
    }

    // Place a bid on a given product
    public Bid placeBid(Long productId, Customer customer, BigDecimal bidAmount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Auction product not found"));

        BigDecimal currentHighestBid = getHighestBidAmount(product);
        if (bidAmount.compareTo(currentHighestBid) <= 0) {
            throw new RuntimeException("Bid amount must be higher than the current highest bid.");
        }

        Bid newBid = new Bid();
        newBid.setProduct(product);
        newBid.setCustomer(customer);
        newBid.setBidAmount(bidAmount);
        newBid.setBidTime(LocalDateTime.now());

        logger.info("Placing bid: {}", newBid);
        return bidRepository.save(newBid);
    }

    // Get the highest bid amount for a product
    public BigDecimal getHighestBidAmount(Product product) {
        List<Bid> bids = bidRepository.findByProductOrderByBidAmountDesc(product);
        return bids.isEmpty() ? product.getStartingPrice() : bids.getFirst().getBidAmount();
    }

    // Retrieve all bids by a customer
    public List<Bid> getBidsByCustomer(Customer customer) {
        return bidRepository.findByCustomer(customer);
    }

    // Retrieve all bids for a product
    public List<Bid> getBidsForProduct(Product product) {
        return bidRepository.findByProductOrderByBidAmountDesc(product);
    }
}
