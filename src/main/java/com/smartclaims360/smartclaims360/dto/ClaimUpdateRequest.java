package com.smartclaims360.smartclaims360.dto;

import com.smartclaims360.smartclaims360.entity.ClaimType;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request object for updating an existing claim")
public class ClaimUpdateRequest {

    @NotBlank(message = "Claimant name cannot be blank")
    @Schema(description = "Name of the person filing the claim", example = "John Doe", required = true)
    private String claimantName;

    @NotNull(message = "Claim amount cannot be null")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than 0")
    @Schema(description = "Monetary amount of the claim", example = "1500.00", required = true)
    private BigDecimal claimAmount;

    @NotNull(message = "Claim type cannot be null")
    @Schema(description = "Type of insurance claim", example = "AUTO", required = true)
    private ClaimType claimType;

    @Schema(description = "Current processing status of the claim", example = "REVIEW")
    private ClaimStatus status;
}
