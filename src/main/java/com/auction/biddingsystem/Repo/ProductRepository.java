package com.auction.biddingsystem.Repo;

import com.auction.biddingsystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByAuctionStartTimeBeforeAndAuctionEndTimeAfter(LocalDateTime start, LocalDateTime end );
}
