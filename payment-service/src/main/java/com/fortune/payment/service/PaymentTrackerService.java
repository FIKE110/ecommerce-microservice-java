package com.fortune.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortune.Event;
import com.fortune.EventType;
import com.fortune.payment.config.PaymentResponse;
import com.fortune.payment.repository.PaymentResponseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class PaymentTrackerService {

    private final PaymentResponseRepository paymentResponseRepository;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;
    private final RabbitMQService rabbitMQService;

    public PaymentTrackerService(PaymentResponseRepository paymentResponseRepository, @Qualifier("interswitchPaymentService") PaymentService paymentService, ObjectMapper objectMapper, RabbitMQService rabbitMQService) {
        this.paymentResponseRepository = paymentResponseRepository;
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
        this.rabbitMQService = rabbitMQService;
    }

    @Scheduled(fixedRate = 60000)
    public void checkPendingPayments(){
        Page<PaymentResponse> payments;
        int page=0;
        int size=50;
        do{
            payments=paymentResponseRepository.findByInvoiceStatus("PENDING", PageRequest.of(page, size));
            payments.forEach(p->{
                String response=paymentService.fetchPaymentByReference(p.getReference());
                try {
                    PaymentResponse invoiceresponse=objectMapper.readValue(response,PaymentResponse.class);
                    if(!invoiceresponse.getInvoiceStatus().equals("PENDING")){
                        p.setInvoiceStatus(invoiceresponse.getInvoiceStatus());
                        paymentResponseRepository.save(p);
                        rabbitMQService.sendMessage(Event.builder()
                                        .eventType(p.getInvoiceStatus().equalsIgnoreCase("PAID") ?
                                                EventType.ORDER_PAID : EventType.ORDER_FAIL)
                                        .id(UUID.randomUUID().toString())
                                        .message(Map.of(
                                                "reference_number",p.getReference()
                                        ))
                                .build());
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
            page++;
        }
        while(payments.hasNext());
    }
}
