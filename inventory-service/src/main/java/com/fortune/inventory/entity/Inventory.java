package com.fortune.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="inventory")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @SequenceGenerator(name = "inventory_sequence",sequenceName = "inventory_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "inventory_sequence")
    private Long id;

    @Column(unique=true)
    private UUID productId;
    private Long quantity;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}