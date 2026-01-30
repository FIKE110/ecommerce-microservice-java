package com.fortune.order.config;


import com.fortune.order.model.PaymentResponseCheckout;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name="payment-service")
public interface PaymentClient {
    @PostMapping("initialize")
    public String initiatePayment(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request);
    @GetMapping("/invoice/{reference}")
    public Map<String,String> getInvoice(@RequestHeader("Authorization") String token, @PathVariable("reference") String reference);
}
