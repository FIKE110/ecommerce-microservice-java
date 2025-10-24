package com.fortune.payment.service;


import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface PaymentService {

    String initializePayment(Map<String, String> params) throws JsonProcessingException;
    void processPayment();
    void validatePayment();
    void refundPayment();
    String fetchPaymentByReference(String reference);
}
