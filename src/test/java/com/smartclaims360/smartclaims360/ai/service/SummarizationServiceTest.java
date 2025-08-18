package com.smartclaims360.smartclaims360.ai.service;

import com.smartclaims360.smartclaims360.ai.dto.FraudScoreResponse;
import com.smartclaims360.smartclaims360.ai.dto.ValidationResponse;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummarizationServiceTest {

    @Mock
    private AiValidationService aiValidationService;

    @Mock
    private FraudScoringService fraudScoringService;

    @InjectMocks
    private SummarizationService summarizationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(summarizationService, "summarizationEnabled", true);
    }

    @Test
    void testSummarizeValidClaim() {
        Claim claim = createTestClaim();
        
        ValidationResponse validationResponse = new ValidationResponse(true, Arrays.asList(), Arrays.asList("Consider additional documentation"));
        FraudScoreResponse fraudResponse = new FraudScoreResponse(new BigDecimal("0.25"), "LOW", "Low fraud risk");
        
        when(aiValidationService.validateClaim(claim)).thenReturn(validationResponse);
        when(fraudScoringService.scoreClaim(claim)).thenReturn(fraudResponse);

        String summary = summarizationService.summarize(claim);

        assertNotNull(summary);
        assertTrue(summary.contains("CLAIM SUMMARY"));
        assertTrue(summary.contains("John Doe"));
        assertTrue(summary.contains("$1,000.00"));
        assertTrue(summary.contains("AUTO"));
        assertTrue(summary.contains("VALIDATION STATUS: VALID"));
        assertTrue(summary.contains("FRAUD RISK: LOW"));
        assertTrue(summary.contains("Consider additional documentation"));
    }

    @Test
    void testSummarizeInvalidClaim() {
        Claim claim = createTestClaim();
        
        ValidationResponse validationResponse = new ValidationResponse(false, Arrays.asList("Invalid claim type"), Arrays.asList());
        FraudScoreResponse fraudResponse = new FraudScoreResponse(new BigDecimal("0.75"), "HIGH", "High fraud risk");
        
        when(aiValidationService.validateClaim(claim)).thenReturn(validationResponse);
        when(fraudScoringService.scoreClaim(claim)).thenReturn(fraudResponse);

        String summary = summarizationService.summarize(claim);

        assertNotNull(summary);
        assertTrue(summary.contains("VALIDATION STATUS: INVALID"));
        assertTrue(summary.contains("Invalid claim type"));
        assertTrue(summary.contains("FRAUD RISK: HIGH"));
        assertTrue(summary.contains("0.75"));
    }

    @Test
    void testSummarizationDisabled() {
        ReflectionTestUtils.setField(summarizationService, "summarizationEnabled", false);

        Claim claim = createTestClaim();

        String summary = summarizationService.summarize(claim);

        assertEquals("Claim summarization is disabled", summary);
    }

    private Claim createTestClaim() {
        Claim claim = new Claim();
        claim.setId(UUID.randomUUID());
        claim.setClaimantName("John Doe");
        claim.setFirstName("John");
        claim.setLastName("Doe");
        claim.setDateOfBirth(LocalDate.of(1985, 3, 15));
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setClaimType(ClaimType.AUTO);
        claim.setStatus(ClaimStatus.NEW);
        claim.setCreatedAt(LocalDateTime.now());
        return claim;
    }
}
