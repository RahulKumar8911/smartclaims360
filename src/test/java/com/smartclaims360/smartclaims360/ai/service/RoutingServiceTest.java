package com.smartclaims360.smartclaims360.ai.service;

import com.smartclaims360.smartclaims360.ai.dto.FraudScoreResponse;
import com.smartclaims360.smartclaims360.ai.dto.RoutingSuggestion;
import com.smartclaims360.smartclaims360.ai.dto.ValidationResponse;
import com.smartclaims360.smartclaims360.entity.Claim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutingServiceTest {

    @Mock
    private AiValidationService aiValidationService;

    @Mock
    private FraudScoringService fraudScoringService;

    @InjectMocks
    private RoutingService routingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(routingService, "routingEnabled", true);
    }

    @Test
    void testSuggestValidLowRiskClaim() {
        Claim claim = createTestClaim("AUTO");
        
        ValidationResponse validationResponse = new ValidationResponse(true, Arrays.asList(), Arrays.asList());
        FraudScoreResponse fraudResponse = new FraudScoreResponse(new BigDecimal("0.25"), "LOW", "Low fraud risk");
        
        when(aiValidationService.validateClaim(claim)).thenReturn(validationResponse);
        when(fraudScoringService.scoreClaim(claim)).thenReturn(fraudResponse);

        RoutingSuggestion suggestion = routingService.suggest(claim);

        assertEquals("AUTO", suggestion.getQueue());
        assertTrue(suggestion.getReason().contains("Standard processing"));
        assertTrue(suggestion.getReason().contains("low fraud risk"));
    }

    @Test
    void testSuggestValidMediumRiskClaim() {
        Claim claim = createTestClaim("HEALTH");
        
        ValidationResponse validationResponse = new ValidationResponse(true, Arrays.asList(), Arrays.asList());
        FraudScoreResponse fraudResponse = new FraudScoreResponse(new BigDecimal("0.50"), "MEDIUM", "Medium fraud risk");
        
        when(aiValidationService.validateClaim(claim)).thenReturn(validationResponse);
        when(fraudScoringService.scoreClaim(claim)).thenReturn(fraudResponse);

        RoutingSuggestion suggestion = routingService.suggest(claim);

        assertEquals("HEALTH", suggestion.getQueue());
        assertTrue(suggestion.getReason().contains("medium fraud risk"));
    }

    @Test
    void testSuggestHighRiskClaim() {
        Claim claim = createTestClaim("AUTO");
        
        ValidationResponse validationResponse = new ValidationResponse(true, Arrays.asList(), Arrays.asList());
        FraudScoreResponse fraudResponse = new FraudScoreResponse(new BigDecimal("0.80"), "HIGH", "High fraud risk");
        
        when(aiValidationService.validateClaim(claim)).thenReturn(validationResponse);
        when(fraudScoringService.scoreClaim(claim)).thenReturn(fraudResponse);

        RoutingSuggestion suggestion = routingService.suggest(claim);

        assertEquals("MANUAL_REVIEW", suggestion.getQueue());
        assertTrue(suggestion.getReason().contains("High fraud risk detected"));
    }

    @Test
    void testSuggestInvalidClaim() {
        Claim claim = createTestClaim("AUTO");
        
        ValidationResponse validationResponse = new ValidationResponse(false, Arrays.asList("Invalid claim type"), Arrays.asList());
        FraudScoreResponse fraudResponse = new FraudScoreResponse(new BigDecimal("0.25"), "LOW", "Low fraud risk");
        
        when(aiValidationService.validateClaim(claim)).thenReturn(validationResponse);
        when(fraudScoringService.scoreClaim(claim)).thenReturn(fraudResponse);

        RoutingSuggestion suggestion = routingService.suggest(claim);

        assertEquals("MANUAL_REVIEW", suggestion.getQueue());
        assertTrue(suggestion.getReason().contains("Claim failed validation"));
    }

    @Test
    void testRoutingDisabled() {
        ReflectionTestUtils.setField(routingService, "routingEnabled", false);

        Claim claim = createTestClaim("AUTO");

        RoutingSuggestion suggestion = routingService.suggest(claim);

        assertEquals("MANUAL_REVIEW", suggestion.getQueue());
        assertEquals("Routing service is disabled", suggestion.getReason());
    }

    private Claim createTestClaim(String claimType) {
        Claim claim = new Claim();
        claim.setId(UUID.randomUUID());
        claim.setClaimantName("John Doe");
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setClaimType(claimType);
        claim.setStatus("NEW");
        claim.setCreatedAt(LocalDateTime.now());
        return claim;
    }
}
