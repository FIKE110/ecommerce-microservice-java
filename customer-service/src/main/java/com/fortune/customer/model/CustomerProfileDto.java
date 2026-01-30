package com.fortune.customer.model;

import com.fortune.customer.enumeration.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerProfileDto {

        private Long id;
        @Column(unique = true)
        private String username;
        private String firstName;
        private String lastName;
        @Column(unique = false,nullable = true)
        private String address;
        @Enumerated(EnumType.STRING)
        private Gender gender;
        @Column(unique = false,nullable = true)
        private LocalDate birthDate;
        @CreationTimestamp
        private LocalDateTime createdAt;
        @UpdateTimestamp
        private LocalDateTime updatedAt;

}
