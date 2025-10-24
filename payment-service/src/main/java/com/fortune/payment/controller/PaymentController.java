package com.fortune.payment.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fortune.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("${api.url}")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(@Qualifier("interswitchPaymentService") PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("initialize")
    public String initiatePayment(@RequestBody Map<String, String> request) throws JsonProcessingException {
        return paymentService.initializePayment(request);
    }


}
