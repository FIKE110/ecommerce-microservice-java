package com.fortune.order.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponseCheckout {
    private int id;
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

