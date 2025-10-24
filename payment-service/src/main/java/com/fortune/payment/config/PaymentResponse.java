package com.fortune.payment.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    private String customerName;
    private String customerEmail;
    private String description;
    private String merchantCode;
    private String payableCode;
    private int payableId;
    private String reference;
    private String invoiceLink;
    private String invoiceStatus;
    private String responseCode;
    private double tax;
    private double discountPercent;
    private String address;
    private double shippingFee;
    private Long dueDate;
    private Long createdDate;
    private Long lastUpdated;
    private String currencyCode;
}

