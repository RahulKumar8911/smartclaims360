package com.smartclaims360.smartclaims360.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing fraud risk analysis results")
public class FraudScoreResponse {
    
    @Schema(description = "Calculated fraud risk score from 0.0 (no risk) to 1.0 (high risk)", example = "0.25", minimum = "0.0", maximum = "1.0")
    private BigDecimal fraudScore;
    
    @Schema(description = "Risk level classification based on fraud score", example = "LOW", allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private String riskLevel;
    
    @Schema(description = "Detailed explanation of the fraud risk assessment including contributing factors", example = "Claim amount within normal range. Standard claim type frequency. No suspicious patterns detected.")
    private String explanation;
}
