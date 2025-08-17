package com.smartclaims360.smartclaims360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotBlank(message = "Claimant name cannot be blank")
    @Column(nullable = false)
    private String claimantName;

    @NotNull(message = "Claim amount cannot be null")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than 0")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal claimAmount;

    @NotBlank(message = "Claim type cannot be blank")
    @Column(nullable = false)
    private String claimType;

    @Column(nullable = false)
    private String status = "NEW";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
