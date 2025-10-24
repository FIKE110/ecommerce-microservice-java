package com.fortune.payment.repository;

import com.fortune.payment.config.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentResponseRepository extends JpaRepository<PaymentResponse,Long> {

    Page<PaymentResponse> findByInvoiceStatus(String status, Pageable pageable);
}
