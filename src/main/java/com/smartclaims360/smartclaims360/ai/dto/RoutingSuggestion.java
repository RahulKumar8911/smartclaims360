package com.smartclaims360.smartclaims360.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Intelligent routing recommendation based on claim analysis")
public class RoutingSuggestion {
    
    @Schema(description = "Recommended processing queue for the claim", example = "AUTO", allowableValues = {"AUTO", "HEALTH", "PROPERTY", "LIFE", "MANUAL_REVIEW"})
    private String queue;
    
    @Schema(description = "Explanation for the routing decision including risk factors", example = "Standard AUTO claim with low fraud risk (0.25). Route to automated processing.")
    private String reason;
}
