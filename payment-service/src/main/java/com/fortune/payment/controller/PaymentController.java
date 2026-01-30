package com.fortune.payment.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fortune.payment.service.PaymentRepoService;
import com.fortune.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("${api.url}")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepoService paymentRepoService;

    public PaymentController(@Qualifier("interswitchPaymentService") PaymentService paymentService, PaymentRepoService paymentRepoService) {
        this.paymentService = paymentService;
        this.paymentRepoService = paymentRepoService;
    }

    @PostMapping("initialize")
    public String initiatePayment(@RequestBody Map<String, String> request) throws JsonProcessingException {
        return paymentService.initializePayment(request);
    }

    @GetMapping("/invoice/{reference}")
    public Map<String,String> getInvoice(@PathVariable("reference") String reference) {
        return Map.of("link",paymentRepoService.getInvoice(reference));
    }


}
