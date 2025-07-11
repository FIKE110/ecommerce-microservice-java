package com.fortune.auth.repository;

import com.fortune.auth.entity.CustomerToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerTokenRepository extends JpaRepository<CustomerToken, Long> {
    Optional<CustomerToken> findByCustomer_Email(String customerEmail);

    Optional<CustomerToken> findByTokenAndCustomer_Email(String token, String customerEmail);
}
