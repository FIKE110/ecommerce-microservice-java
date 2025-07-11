package com.fortune.auth.entity;

import com.fortune.auth.enumeration.TokenPurpose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="tokens")
@Setter
@Getter
public class CustomerToken {
    @Id
    @SequenceGenerator(sequenceName = "token_sequence",name = "token_sequence",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "token_sequence")
    private Long id;
    @Enumerated(EnumType.STRING)
    private TokenPurpose purpose;
    private String token;
    @OneToOne(fetch = FetchType.LAZY)
    private Customer customer;
    private LocalDateTime expiresAt;
}
