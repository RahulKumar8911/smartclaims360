package com.smartclaims360.smartclaims360.ai.service;

import com.smartclaims360.smartclaims360.ai.dto.FraudScoreResponse;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FraudScoringService {

    @Autowired
    private ClaimRepository claimRepository;

    @Value("${ai.scoring.enabled:true}")
    private boolean scoringEnabled;

    public FraudScoreResponse scoreClaim(Claim claim) {
        if (!scoringEnabled) {
            return new FraudScoreResponse(BigDecimal.ZERO, "LOW", "Fraud scoring is disabled");
        }

        double score = calculateFraudScore(claim);
        BigDecimal fraudScore = BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
        
        String riskLevel = getRiskLevel(fraudScore);
        String explanation = generateExplanation(claim, fraudScore);

        if (claim.getId() != null) {
            claim.setFraudScore(fraudScore);
            claimRepository.save(claim);
        }

        return new FraudScoreResponse(fraudScore, riskLevel, explanation);
    }

    private double calculateFraudScore(Claim claim) {
        double score = 0.0;
        List<Claim> historicalClaims = claimRepository.findAll();

        if (historicalClaims.isEmpty()) {
            return 0.1;
        }

        double amountScore = calculateAmountAnomalyScore(claim, historicalClaims);
        score += amountScore * 0.4;

        double typeScore = calculateClaimTypeFrequencyScore(claim, historicalClaims);
        score += typeScore * 0.3;

        double nameScore = calculateRepeatedNameScore(claim, historicalClaims);
        score += nameScore * 0.3;

        return Math.min(1.0, Math.max(0.0, score));
    }

    private double calculateAmountAnomalyScore(Claim claim, List<Claim> historicalClaims) {
        List<Double> amounts = historicalClaims.stream()
                .map(c -> c.getClaimAmount().doubleValue())
                .collect(Collectors.toList());

        double mean = amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = amounts.stream()
                .mapToDouble(amount -> Math.pow(amount - mean, 2))
                .average().orElse(0.0);
        double stdDev = Math.sqrt(variance);

        if (stdDev == 0) return 0.0;

        double zScore = Math.abs((claim.getClaimAmount().doubleValue() - mean) / stdDev);
        
        return Math.min(1.0, zScore / 3.0);
    }

    private double calculateClaimTypeFrequencyScore(Claim claim, List<Claim> historicalClaims) {
        Map<String, Long> typeFrequency = historicalClaims.stream()
                .collect(Collectors.groupingBy(Claim::getClaimType, Collectors.counting()));

        long totalClaims = historicalClaims.size();
        long typeCount = typeFrequency.getOrDefault(claim.getClaimType(), 0L);
        
        double frequency = (double) typeCount / totalClaims;
        
        return Math.max(0.0, 1.0 - (frequency * 2.0));
    }

    private double calculateRepeatedNameScore(Claim claim, List<Claim> historicalClaims) {
        long nameCount = historicalClaims.stream()
                .filter(c -> c.getClaimantName().equalsIgnoreCase(claim.getClaimantName()))
                .count();

        if (nameCount <= 1) return 0.0;
        
        return Math.min(1.0, (nameCount - 1) * 0.2);
    }

    private String getRiskLevel(BigDecimal fraudScore) {
        double score = fraudScore.doubleValue();
        if (score >= 0.7) return "HIGH";
        if (score >= 0.4) return "MEDIUM";
        return "LOW";
    }

    private String generateExplanation(Claim claim, BigDecimal fraudScore) {
        double score = fraudScore.doubleValue();
        
        if (score >= 0.7) {
            return "High fraud risk detected based on anomalous patterns in amount, claim type frequency, or repeated claimant";
        } else if (score >= 0.4) {
            return "Medium fraud risk - some unusual patterns detected that warrant additional review";
        } else {
            return "Low fraud risk - claim appears consistent with historical patterns";
        }
    }
}
