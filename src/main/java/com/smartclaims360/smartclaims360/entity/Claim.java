package com.smartclaims360.smartclaims360.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Insurance claim entity with AI-powered fraud detection capabilities")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    @Schema(description = "Unique identifier for the claim", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @NotBlank(message = "Claimant name cannot be blank")
    @Column(nullable = false)
    @Schema(description = "Full name of the person filing the claim", example = "John Doe", required = true)
    private String claimantName;

    @NotNull(message = "Claim amount cannot be null")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than 0")
    @Column(nullable = false, precision = 19, scale = 2)
    @Schema(description = "Monetary amount being claimed in USD", example = "1500.00", required = true)
    private BigDecimal claimAmount;

    @NotNull(message = "Claim type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Type of insurance claim", example = "AUTO", required = true)
    private ClaimType claimType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Current processing status of the claim", example = "NEW")
    private ClaimStatus status = ClaimStatus.NEW;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Schema(description = "Timestamp when the claim was created", example = "2025-08-17T14:30:00")
    private LocalDateTime createdAt;

    @Column(precision = 3, scale = 2)
    @Schema(description = "AI-calculated fraud risk score from 0.00 (no risk) to 1.00 (high risk)", example = "0.25", minimum = "0.00", maximum = "1.00")
    private BigDecimal fraudScore;

}
