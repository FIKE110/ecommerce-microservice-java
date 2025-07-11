package com.fortune.auth.repository;

import com.fortune.auth.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    UserDetails findByEmailOrUsername(String email, String username);

    Optional<Customer> findByEmail(String email);
}
