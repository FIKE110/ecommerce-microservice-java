package com.fortune.payment;

import com.fortune.payment.config.AppConfigProp;
import com.fortune.payment.config.RsaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableConfigurationProperties({AppConfigProp.class, RsaProperties.class})
public class PaymentApplication {

   public static void main(String[] args) {
       SpringApplication.run(PaymentApplication.class, args);
   }
}