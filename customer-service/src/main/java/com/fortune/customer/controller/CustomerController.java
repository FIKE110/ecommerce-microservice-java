package com.fortune.customer.controller;

import com.fortune.utils.ApiDataResponse;
import com.fortune.utils.ApiResponse;
import com.fortune.utils.DataWrapper;
import com.fortune.utils.MessageInString;
import com.fortune.customer.dto.CustomerProfileDto;
import com.fortune.customer.enumeration.CustomerResponseCode;
import com.fortune.customer.request.CustomerCreateRequest;
import com.fortune.customer.request.CustomerUpdateRequest;
import com.fortune.customer.service.CustomerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"${api.version}","${api.version}/"})
public class CustomerController {

    private final CustomerProfileService customerProfileService;

    public CustomerController(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiDataResponse<DataWrapper<CustomerResponseCode, MessageInString>>> createCustomerProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CustomerCreateRequest customerRequest) {
        customerProfileService.createCustomerProfile(jwt.getSubject(), customerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.data(
                                CustomerResponseCode.PROFILE_CREATED,
                                new MessageInString("Profile successfully created")
                        )
        );
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CustomerProfileDto> getCustomerProfile(@AuthenticationPrincipal Jwt jwt) {
        CustomerProfileDto profile=customerProfileService.getCustomerProfile(jwt.getSubject());
        return ResponseEntity.ok(
                                profile
        );
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDataResponse<DataWrapper<CustomerResponseCode, MessageInString>>> createCustomerProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CustomerUpdateRequest customerRequest) {
        customerProfileService.updateCustomerProfile(jwt.getSubject(),customerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.data(
                                CustomerResponseCode.PROFILE_CREATED,
                                new MessageInString("Profile updated successfully")
                        )
        );
    }

}
