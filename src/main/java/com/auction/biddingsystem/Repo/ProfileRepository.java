package com.auction.biddingsystem.Repo;

import com.auction.biddingsystem.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Profile findByCustomerId(Long customerId);
}
