//package com.fortune.test;
//
//import com.fortune.payment.service.InterswitchService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.web.client.RestClient;
//
//import java.io.PrintStream;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//
//@SpringBootTest(classes = InterswitchService.class)
//class InterswitchServiceTest {
//
//    private InterswitchService interswitchService;
//
//    @BeforeEach
//    void setUp() {
//        // Mock RestClient inside the service
//        interswitchService = new InterswitchService();
//    }
//
//    @Test
//    void testInitializePayment() {
//        // Arrange (sample payload from you)
//        Map<String, String> params = Map.of(
//                "order_id", "Default_Payable_MX253560",
//                "txn_ref", "TXN123456",
//                "amount", "21600",
//                "email", "chihurumikechukwu05@gmail.com",
//                "first_name", "cfcfcf",
//                "last_name", "Customer",
//                "address", "jbjbjbjbj",
//                "item_name", "jjbj",
//                "quantity", "4",
//                "item_amount", "5400"
//        );
//
//        // Fake API response
//        String fakeResponse = """
//            {
//              "status": "SUCCESS",
//              "invoiceId": "INV-12345",
//              "paymentUrl": "https://qa.interswitchng.com/pay/INV-12345"
//            }
//            """;
//
//        // Mock RestClient behavior
//        RestClient.RequestBodySpec mockRequest = Mockito.mock(RestClient.RequestBodySpec.class);
//        RestClient.RequestHeadersSpec mockHeaders = Mockito.mock(RestClient.RequestHeadersSpec.class);
//        RestClient.ResponseSpec mockResponse = Mockito.mock(RestClient.ResponseSpec.class);
//
//        Mockito.when(restClient.post()).thenReturn(mockRequest);
//        Mockito.when(mockRequest.uri(any(String.class))).thenReturn(mockRequest);
//        Mockito.when(mockRequest.contentType(any())).thenReturn(mockRequest);
//        Mockito.when(mockRequest.body(any())).thenReturn(mockHeaders);
//        Mockito.when(mockHeaders.retrieve()).thenReturn(mockResponse);
//        Mockito.when(mockResponse.body(String.class)).thenReturn(fakeResponse);
//
//        // Act
//        String result = interswitchService.initializePayment(params);
//
//        // Assert
//        assertThat(result).contains("SUCCESS");
//        assertThat(result).contains("paymentUrl");
//    }
//}
