package com.fortune.payment.service;

import java.util.Map;

public abstract class PaystackService implements  PaymentService {
    @Override
    public String initializePayment(Map<String, String> params) {
        return null;
    }

    @Override
    public void processPayment() {

    }

    @Override
    public void validatePayment() {

    }

    @Override
    public void refundPayment() {

    }
}
