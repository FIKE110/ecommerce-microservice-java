package com.fortune.customer.service;

import com.fortune.customer.dto.CustomerProfileDto;
import com.fortune.customer.entity.CustomerProfile;
import com.fortune.customer.repository.CustomerProfileRepository;
import com.fortune.customer.request.CustomerCreateRequest;
import com.fortune.customer.request.CustomerUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;

    public CustomerProfileService(CustomerProfileRepository customerProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
    }

    @Transactional
    public void createCustomerProfile(String username,CustomerCreateRequest request) {
        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setFirstName(request.firstName());
        customerProfile.setLastName(request.lastName());
        customerProfile.setAddress(request.address());
        customerProfile.setGender(request.gender());
        customerProfile.setBirthDate(request.birthDate());
        customerProfile.setUsername(username);
        customerProfileRepository.save(customerProfile);
    }

    public CustomerProfileDto getCustomerProfile(String customer) {
         CustomerProfile profile=customerProfileRepository.findByUsername(customer).orElseThrow(()-> new RuntimeException("Customer not found"));
         return new CustomerProfileDto(profile.getFirstName(), profile.getLastName(), profile.getAddress(),profile.getBirthDate(),profile.getGender());
    }

    @Transactional
    public void updateCustomerProfile(String customer,CustomerUpdateRequest request) {
        CustomerProfile profile= customerProfileRepository.findByUsername(customer).orElseThrow(()-> new RuntimeException("Customer not found"));
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setAddress(request.address());
        profile.setGender(request.gender());
        profile.setBirthDate(request.birthDate());customerProfileRepository.save(profile);
    }
}