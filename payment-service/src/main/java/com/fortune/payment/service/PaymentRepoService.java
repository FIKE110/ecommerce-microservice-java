package com.fortune.payment.service;

import com.fortune.payment.config.PaymentResponse;
import com.fortune.payment.repository.PaymentResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentRepoService {

    private final PaymentResponseRepository paymentResponseRepository;

    public PaymentRepoService(PaymentResponseRepository paymentResponseRepository) {
        this.paymentResponseRepository = paymentResponseRepository;
    }

    public String getInvoice(String reference) {
        return paymentResponseRepository.findByReference(reference)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Payment not found for reference: " + reference
                        )
                ).getInvoiceLink();
    }

}
