package com.smartclaims360.smartclaims360.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudScoreResponse {
    
    private BigDecimal fraudScore;
    private String riskLevel;
    private String explanation;
}
