package com.fortune.order.config;


import com.fortune.order.model.PaymentResponseCheckout;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name="payment-service")
public interface PaymentClient {
    @PostMapping("initialize")
    public String initiatePayment(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request);
}
