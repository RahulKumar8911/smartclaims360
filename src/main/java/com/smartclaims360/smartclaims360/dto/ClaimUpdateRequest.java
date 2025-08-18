package com.smartclaims360.smartclaims360.dto;

import com.smartclaims360.smartclaims360.entity.ClaimType;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Request object for updating an existing claim")
public class ClaimUpdateRequest {

    @NotBlank(message = "Claimant name cannot be blank")
    @Schema(description = "Name of the person filing the claim", example = "John Doe", required = true)
    private String claimantName;

    @NotBlank(message = "First name cannot be blank")
    @Schema(description = "First name of the claimant", example = "John", required = true)
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Schema(description = "Last name of the claimant", example = "Doe", required = true)
    private String lastName;

    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be in the past")
    @Schema(description = "Date of birth of the claimant", example = "1985-03-15", required = true)
    private LocalDate dateOfBirth;

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
