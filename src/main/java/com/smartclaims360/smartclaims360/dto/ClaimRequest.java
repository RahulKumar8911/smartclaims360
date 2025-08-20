package com.smartclaims360.smartclaims360.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
// dto classes comment

@Data
@Schema(description = "Request payload for creating a new insurance claim")
public class ClaimRequest {

    @NotBlank(message = "Claimant name cannot be blank")
    @Schema(description = "Full name of the person filing the claim", example = "John Doe", required = true)
    private String claimantName;

    @NotNull(message = "Claim amount cannot be null")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than 0")
    @Schema(description = "Monetary amount being claimed in USD", example = "1500.00", required = true)
    private BigDecimal claimAmount;

    @NotBlank(message = "Claim type cannot be blank")
    @Schema(description = "Type of insurance claim", example = "AUTO", allowableValues = {"AUTO", "HEALTH", "PROPERTY", "LIFE"}, required = true)
    private String claimType;

}
