package com.fortune.customer.request;

import com.fortune.customer.enumeration.Gender;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CustomerUpdateRequest (
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        LocalDate birthDate,
        @NotBlank
        String address,
        Gender gender
){
}
