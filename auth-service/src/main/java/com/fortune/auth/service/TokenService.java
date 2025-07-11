package com.fortune.auth.service;

import com.fortune.auth.entity.Customer;
import com.fortune.auth.entity.CustomerToken;
import com.fortune.auth.enumeration.TokenPurpose;
import com.fortune.auth.repository.CustomerRepository;
import com.fortune.auth.repository.CustomerTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final CustomerTokenRepository customerTokenRepository;
    private final CustomerRepository customerRepository;

    public TokenService(CustomerTokenRepository customerTokenRepository, CustomerRepository customerRepository) {
        this.customerTokenRepository = customerTokenRepository;
        this.customerRepository = customerRepository;
    }

    public String generateToken() {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        var token=random.nextInt(1_000_000_000);
        return String.valueOf(token);
    }

    public void validateToken(String email,String token) {
        customerTokenRepository.findByTokenAndCustomer_Email(token,email);
    }

    public void generateVerificationOtp(String email) {
        log.info(email);
        CustomerToken token=customerTokenRepository.findByCustomer_Email(email).orElse(new CustomerToken());
        Customer customer=customerRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Customer not found"));
        token.setToken(generateToken());
        token.setPurpose(TokenPurpose.VERIFICATION);
        token.setCustomer(customer);
        token.setExpiresAt(LocalDateTime.now());
        log.info(token.getToken());
    }

}
