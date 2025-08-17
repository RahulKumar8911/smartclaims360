package com.smartclaims360.smartclaims360.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing claim validation results and AI-powered recommendations")
public class ValidationResponse {
    
    @Schema(description = "Whether the claim passes all validation rules", example = "true")
    private boolean valid;
    
    @Schema(description = "List of validation failure reasons if claim is invalid", example = "[\"Claimant name cannot be blank\", \"Claim amount must be greater than 0\"]")
    private List<String> reasons;
    
    @Schema(description = "AI-generated hints and recommendations for improving the claim", example = "[\"Consider adding additional documentation for faster processing\", \"Claim amount is within normal range for AUTO claims\"]")
    private List<String> llmHints;
}
