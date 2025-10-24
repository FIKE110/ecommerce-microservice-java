package com.fortune.order.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortune.order.model.PaymentResponseCheckout;

import java.io.IOException;

public class CheckoutUrlDeserializer extends JsonDeserializer<PaymentResponseCheckout> {
    @Override
    public PaymentResponseCheckout deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String json = p.getText(); // raw string
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, PaymentResponseCheckout.class);
    }
}
