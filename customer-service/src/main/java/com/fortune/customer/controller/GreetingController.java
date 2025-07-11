package com.fortune.customer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("${api.version}/greetings")
public class GreetingController {

    @Value("${eureka.instance.instance-id}")
    private String id;

    @GetMapping
    public String greeting() {
        log.info("Message sent to customer service");
        return "Greetings from Customers service! "+id;
    }
}
