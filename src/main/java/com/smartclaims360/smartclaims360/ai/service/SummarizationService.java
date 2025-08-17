package com.smartclaims360.smartclaims360.ai.service;

import com.smartclaims360.smartclaims360.ai.dto.FraudScoreResponse;
import com.smartclaims360.smartclaims360.ai.dto.ValidationResponse;
import com.smartclaims360.smartclaims360.entity.Claim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
public class SummarizationService {

    @Autowired
    private AiValidationService aiValidationService;

    @Autowired
    private FraudScoringService fraudScoringService;

    @Value("${ai.summarization.enabled:true}")
    private boolean summarizationEnabled;

    public String summarize(Claim claim) {
        if (!summarizationEnabled) {
            return "Claim summarization is disabled";
        }

        StringBuilder summary = new StringBuilder();
        
        summary.append("CLAIM SUMMARY\n");
        summary.append("=============\n");
        summary.append("ID: ").append(claim.getId()).append("\n");
        summary.append("Claimant: ").append(claim.getClaimantName()).append("\n");
        summary.append("Amount: ").append(formatCurrency(claim.getClaimAmount())).append("\n");
        summary.append("Type: ").append(claim.getClaimType()).append("\n");
        summary.append("Status: ").append(claim.getStatus()).append("\n");
        summary.append("Created: ").append(claim.getCreatedAt()).append("\n");

        ValidationResponse validation = aiValidationService.validateClaim(claim);
        summary.append("\nVALIDATION STATUS: ").append(validation.isValid() ? "VALID" : "INVALID").append("\n");
        
        if (!validation.getReasons().isEmpty()) {
            summary.append("Validation Issues:\n");
            for (String reason : validation.getReasons()) {
                summary.append("- ").append(reason).append("\n");
            }
        }

        if (!validation.getLlmHints().isEmpty()) {
            summary.append("AI Recommendations:\n");
            for (String hint : validation.getLlmHints()) {
                summary.append("- ").append(hint).append("\n");
            }
        }

        FraudScoreResponse fraudScore = fraudScoringService.scoreClaim(claim);
        summary.append("\nFRAUD RISK: ").append(fraudScore.getRiskLevel()).append("\n");
        summary.append("Fraud Score: ").append(fraudScore.getFraudScore()).append("/1.00\n");
        summary.append("Risk Analysis: ").append(fraudScore.getExplanation()).append("\n");

        return summary.toString();
    }

    private String formatCurrency(java.math.BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(amount);
    }
}
