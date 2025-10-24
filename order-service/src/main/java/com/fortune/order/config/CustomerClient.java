package com.fortune.order.config;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="customer-service")
public interface CustomerClient {
}
