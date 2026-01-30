package com.fortune.customer.dto;

import com.fortune.customer.enumeration.Gender;

import java.time.LocalDate;

public record CustomerProfileDto(
        String username,
        String firstName,
        String lastName,
        String address,
        LocalDate birthDate,
        Gender gender
) {
}
