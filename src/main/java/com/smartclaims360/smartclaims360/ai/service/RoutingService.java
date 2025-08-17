package com.smartclaims360.smartclaims360.ai.service;

import com.smartclaims360.smartclaims360.ai.dto.RoutingSuggestion;
import com.smartclaims360.smartclaims360.ai.dto.ValidationResponse;
import com.smartclaims360.smartclaims360.ai.dto.FraudScoreResponse;
import com.smartclaims360.smartclaims360.entity.Claim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RoutingService {

    @Autowired
    private AiValidationService aiValidationService;

    @Autowired
    private FraudScoringService fraudScoringService;

    @Value("${ai.routing.enabled:true}")
    private boolean routingEnabled;

    public RoutingSuggestion suggest(Claim claim) {
        if (!routingEnabled) {
            return new RoutingSuggestion("MANUAL_REVIEW", "Routing service is disabled");
        }

        ValidationResponse validation = aiValidationService.validateClaim(claim);
        FraudScoreResponse fraudScore = fraudScoringService.scoreClaim(claim);

        if (!validation.isValid()) {
            return new RoutingSuggestion("MANUAL_REVIEW", 
                "Claim failed validation: " + String.join(", ", validation.getReasons()));
        }

        if (fraudScore.getFraudScore().compareTo(new BigDecimal("0.70")) >= 0) {
            return new RoutingSuggestion("MANUAL_REVIEW", 
                "High fraud risk detected (score: " + fraudScore.getFraudScore() + ")");
        }

        String claimType = claim.getClaimType().toUpperCase();
        String reason = "Standard processing for " + claimType + " claim";
        
        if (fraudScore.getFraudScore().compareTo(new BigDecimal("0.40")) >= 0) {
            reason += " with medium fraud risk (score: " + fraudScore.getFraudScore() + ")";
        } else {
            reason += " with low fraud risk (score: " + fraudScore.getFraudScore() + ")";
        }

        return new RoutingSuggestion(claimType, reason);
    }
}
