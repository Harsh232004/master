package com.auction.biddingsystem.Repo;

import com.auction.biddingsystem.model.Bid;
import com.auction.biddingsystem.model.Customer;
import com.auction.biddingsystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    // Fetch bids for a specific product, ordered by bid amount in descending order
    List<Bid> findByProductOrderByBidAmountDesc(Product product);

    // Fetch all bids placed by a specific customer
    List<Bid> findByCustomer(Customer customer);
}
