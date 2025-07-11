package com.fortune.auth.entity;

import com.fortune.auth.enumeration.AuthMethod;
import com.fortune.auth.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends CustomerDetails{
    @Id
    @SequenceGenerator(
            sequenceName = "customer_sequence",
                name = "customer_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "customer_sequence"
    )
    private Long id;
    @Column(nullable = false,unique = true)
    private String email;
    private String password;
    @Column(nullable = false,unique = true)
    private String username;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String phoneNumber;
    private boolean verified=false;
    @Enumerated(EnumType.STRING)
    private AuthMethod authMethod;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
