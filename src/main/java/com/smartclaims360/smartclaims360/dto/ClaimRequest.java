package com.smartclaims360.smartclaims360.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClaimRequest {

    @NotBlank(message = "Claimant name cannot be blank")
    private String claimantName;

    @NotNull(message = "Claim amount cannot be null")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than 0")
    private BigDecimal claimAmount;

    @NotBlank(message = "Claim type cannot be blank")
    private String claimType;

}
