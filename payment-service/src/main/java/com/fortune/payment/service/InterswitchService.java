package com.fortune.payment.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortune.payment.config.PaymentResponse;
import com.fortune.payment.config.PaymentResponseStatus;
import com.fortune.payment.repository.PaymentResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("interswitchPaymentService")
public class InterswitchService implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(InterswitchService.class);
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final PaymentResponseRepository paymentResponseRepository;
    private String token="Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJsYXN0TmFtZSI6IklrZWNodWt3dSIsImZpcnN0TG9naW4iOmZhbHNlLCJ1c2VyX25hbWUiOiJjaGlodXJ1bWlrZWNodWt3dTA1QGdtYWlsLmNvbSIsIm1vYmlsZU5vIjpudWxsLCJreWNfZG9tYWluIjoiSVNXIiwiY2xpZW50X2Rlc2NyaXB0aW9uIjpudWxsLCJjbGllbnRfaWQiOiJwcm9qZWN0LXgtbWVyY2hhbnQiLCJmaXJzdE5hbWUiOiJGb3J0dW5lIiwiZW1haWxWZXJpZmllZCI6dHJ1ZSwiYXVkIjpbIiIsImt5Yy1zZXJ2aWNlIiwicG9zLXRyYW5zYWN0aW9uLXJlcG9ydGluZyIsImFyYml0ZXItYXBpIiwiZ2VuZXJpYy13YWxsZXQiLCJpc3ctc29ja2V0LWlvIiwiZXNjcm93LXNlcnZpY2UiLCJjYWVzYXIiLCJpbXRvLXNlcnZpY2UiLCJpc3ctcGF5bWVudGdhdGV3YXkiLCJwYXNzcG9ydCIsImlzdy1jb2xsZWN0aW9ucyIsImlzdy1wb3N0LW9mZmljZSIsInZlcnZlLXB1c2gtc2VydmljZSIsIm5hbWUtZW5xdWlyeS1zZXJ2aWNlIiwiaXN3LWNvcmUiLCJ3YWxsZXQtc2VydmljZSIsInZhdWx0IiwicGF5bWF0ZS13YWxsZXQtc2VydmljZSIsIndhbGxldCIsInJldGFpbC1lY29zeXN0ZW0tc2VydmljZSIsImltdG8tb3JkZXItc2VydmljZSIsImltdG8tdHJhbnNhY3Rpb24tc2VydmljZSIsImlwZy1zZXR0bGVtZW50IiwibWVyY2hhbnQtd2FsbGV0IiwiaXN3LW1lcmNoYW50IiwiYXJiaXRlciIsInByb2plY3QteC1tZXJjaGFudCIsIndhbGxldC10cmFuc2Zlci1zZXJ2aWNlIiwicHRzcC1wb3J0YWwtc2VydmljZSJdLCJjbGllbnRfYXV0aG9yaXphdGlvbl9kb21haW4iOiJJU1ciLCJhdXRoZW50aWNhdGVkQnkiOiJQQVNTUE9SVCIsInNjb3BlIjpbInByb2ZpbGUiXSwiZXhwIjoxNzYxMjk4MzAxLCJtb2JpbGVOb1ZlcmlmaWVkIjpmYWxzZSwiY2xpZW50X25hbWUiOiJRdWlja3RlbGxlciBCdXNpbmVzcyIsImNsaWVudF9sb2dvIjpudWxsLCJqdGkiOiI5MjRhY2ZjOC1lZjFjLTRmODktYWJlNC1iZmM0NTQ4M2E1MzIiLCJlbWFpbCI6ImNoaWh1cnVtaWtlY2h1a3d1MDVAZ21haWwuY29tIiwicGFzc3BvcnRJZCI6IjQ5MkE2QjQ0LUZGOEItNEI1MS1BRjg4LTFENTRGNEEzNkE5QSJ9.ap8s7osZBWJYhrWiCT0vmgevK2ijOPjIwlUMqaF1DLErJybiEMPJJquzU815bH3pwmMsHlR_x_ti45OEuwD98Mabui45w_4WB3VgQfTBKApHzq_iTtFgEMS4ellBBZ0ilUdjxiV6wZUlkRw3MI8WhblZ4Y3d5PJPmfOXhY_WQAw9kuCAmpj0-B3U_b9Dp7BQIvmKfkzNdFaRlg_UMpyP-Akud1q-G_m9e3noxlQjEt8jvyQBjtR1-EfY4q4lGZLPKZFV4zfeF78PjwS8FLWWufm2ONcAbI9JzqZxWOlln8QNSGp1LgGF8WZeSfi-GGz5EEt0ORhHmIL3iiD7qY1Biw";

    @Value("${interswitch.merchant_code}")
    private String merchantCode;

    @Value("${interswitch.client_id}")
    private String clientId;

    @Value("${interswitch.secret_key}")
    private String secretKey;

    @Value("${interswitch.currency}")
    private String currencyCode;

    private final RestClient restClient;
    private final RestClient restClient2;

    public InterswitchService(RedisService redisService, ObjectMapper objectMapper, PaymentResponseRepository paymentResponseRepository) {
        this.redisService=redisService;
        this.restClient = RestClient.builder()
                .baseUrl("https://qa.interswitchng.com")
                .build();
        this.restClient2 = RestClient.builder()
                .baseUrl("https://passport.k8.isw.la/passport/oauth/token?grant_type=client_credentials")
                .build();
        this.objectMapper = objectMapper;
        this.paymentResponseRepository = paymentResponseRepository;
    }

    @Override
    public String initializePayment(Map<String, String> params) throws JsonProcessingException {
        String access_key="Bearer " +fetchAuthKey();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", Double.parseDouble(params.get("amount")));
        requestBody.put("customerName", params.get("firstName") + " " + params.get("lastName"));
        requestBody.put("customerEmail", params.get("email"));
        requestBody.put("merchantCode", merchantCode);
        requestBody.put("payableCode", "Default_Payable_" + merchantCode);
        requestBody.put("address", params.get("address"));
        requestBody.put("currency", currencyCode);
        requestBody.put("description", "");                // default empty
        requestBody.put("dueDate", System.currentTimeMillis());  // current timestamp
        requestBody.put("tax", "");                        // default empty
        requestBody.put("note", "");                       // default empty
        requestBody.put("discountType", "percentage");    // default value
        requestBody.put("discount", "");                   // default empty
        requestBody.put("discountPercent", null);         // default null

        String itemsJson = params.get("items");

        List<Map<String, Object>> items =
                objectMapper.readValue(itemsJson, new TypeReference<>() {});

        List<Map<String, Object>> lineItems = items.stream()
                .map(item -> {

                    double price = Double.parseDouble(item.get("price").toString());
                    int quantity = Integer.parseInt(item.get("quantity").toString());


                    return Map.of(
                            "itemName",
                            item.get("productName") != null
                                    ? item.get("productName").toString()
                                    : "Product-" + item.get("productId"),

                            "quantity", item.get("price"),

                            "itemAmount", item.get("quantity")
                    );
                })
                .toList();

        double totalAmount = items.stream()
                .mapToDouble(item ->
                        Double.parseDouble(item.get("price").toString()) *
                                Integer.parseInt(item.get("quantity").toString())
                )
                .sum();

        requestBody.put("amount", totalAmount);
        requestBody.put("lineItems", lineItems);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody));
        PaymentResponse response=restClient.post()
                .uri("/paymentgateway/api/v1/merchant/invoice/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", access_key)
                .header("content-type","application/json")
                .body(requestBody)
                .retrieve()
                .body(PaymentResponse.class);
        assert response != null;
        paymentResponseRepository.save(PaymentResponse.builder()
                        .address(response.getAddress())
                        .amount(response.getAmount())
                        .createdDate(response.getCreatedDate())
                        .currencyCode(currencyCode)
                        .description(response.getDescription())
                        .customerName(response.getCustomerName())
                        .customerEmail(response.getCustomerEmail())
                        .invoiceLink(response.getInvoiceLink())
                        .dueDate(response.getDueDate())
                        .discountPercent(response.getDiscountPercent())
                        .shippingFee(response.getShippingFee())
                        .merchantCode(response.getMerchantCode())
                        .invoiceStatus(response.getInvoiceStatus())
                        .tax(response.getTax())
                        .lastUpdated(response.getLastUpdated())
                        .payableId(response.getPayableId())
                        .responseCode(response.getResponseCode())
                        .reference(response.getReference())
                        .payableCode(response.getPayableCode())
                .build());
        return objectMapper.writeValueAsString(response);
    }

    public String fetchAuthKey(){
        String key=redisService.get("interswitch_key");
        log.info("key : "+key);
        if(key!=null) return key;
        String token=clientId+":"+secretKey;
        String base64=Base64.getEncoder().encodeToString(token.getBytes());
        Map<String,String> response= restClient2.post()
                .uri("")
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Authorization","Basic "+base64)
                .header("accept","application/json")
                .retrieve()
                .toEntity(Map.class).getBody();
        String accessToken=response.get("access_token");
        log.info("access_token="+accessToken);
        redisService.save("interswitch_key",accessToken);
        return accessToken;

    }

    @Override
    public void processPayment() {

    }

    @Override
    public void validatePayment() {
        
    }

    @Override
    public String fetchPaymentByReference(String reference) {
         return restClient.get()
                 .uri(String.format("paymentgateway/api/v1/merchant/invoice/%s",reference))
                 .header("Authorization","Bearer "+fetchAuthKey())
                 .retrieve()
                 .toEntity(String.class).getBody();

                 
    }

    @Override
    public void refundPayment() {

    }
}
