package com.fortune.product.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("${api.url}/greetings")
public class GreetingController {

    @Value("${eureka.instance.instance-id}")
    private String id;

    @GetMapping
    public String greeting() {
        log.info("Message sent to product service");
        return "Greetings from Product service! "+id;
    }
}
