package com.auction.biddingsystem.model;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

@Entity
@Data
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double bidAmount;

    private LocalDateTime bidTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User bidder;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "auction_item_id")
    private AuctionItem auctionItem;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private BidStatus bidStatus;
}
