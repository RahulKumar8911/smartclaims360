package com.smartclaims360.smartclaims360.ai.service;

import com.smartclaims360.smartclaims360.ai.dto.FraudScoreResponse;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.repository.ClaimRepository;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudScoringServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private FraudScoringService fraudScoringService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fraudScoringService, "scoringEnabled", true);
    }

    @Test
    void testScoreClaimWithNoHistoricalData() {
        Claim claim = createTestClaim("John Doe", new BigDecimal("1000.00"), "AUTO");
        when(claimRepository.findAll()).thenReturn(Arrays.asList());

        FraudScoreResponse response = fraudScoringService.scoreClaim(claim);

        assertEquals(new BigDecimal("0.10"), response.getFraudScore());
        assertEquals("LOW", response.getRiskLevel());
    }

    @Test
    void testScoreClaimWithHistoricalData() {
        Claim newClaim = createTestClaim("John Doe", new BigDecimal("1000.00"), "AUTO");
        
        List<Claim> historicalClaims = Arrays.asList(
            createTestClaim("Jane Smith", new BigDecimal("500.00"), "AUTO"),
            createTestClaim("Bob Johnson", new BigDecimal("750.00"), "HEALTH"),
            createTestClaim("Alice Brown", new BigDecimal("600.00"), "AUTO")
        );
        
        when(claimRepository.findAll()).thenReturn(historicalClaims);

        FraudScoreResponse response = fraudScoringService.scoreClaim(newClaim);

        assertNotNull(response.getFraudScore());
        assertTrue(response.getFraudScore().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(response.getFraudScore().compareTo(BigDecimal.ONE) <= 0);
        assertNotNull(response.getRiskLevel());
        assertNotNull(response.getExplanation());
    }

    @Test
    void testScoreClaimHighAmount() {
        Claim newClaim = createTestClaim("John Doe", new BigDecimal("50000.00"), "AUTO");
        
        List<Claim> historicalClaims = Arrays.asList(
            createTestClaim("Jane Smith", new BigDecimal("500.00"), "AUTO"),
            createTestClaim("Bob Johnson", new BigDecimal("750.00"), "AUTO"),
            createTestClaim("Alice Brown", new BigDecimal("600.00"), "AUTO")
        );
        
        when(claimRepository.findAll()).thenReturn(historicalClaims);

        FraudScoreResponse response = fraudScoringService.scoreClaim(newClaim);

        assertTrue(response.getFraudScore().compareTo(new BigDecimal("0.30")) > 0);
    }

    @Test
    void testScoreClaimRepeatedName() {
        Claim newClaim = createTestClaim("John Doe", new BigDecimal("1000.00"), "AUTO");
        
        List<Claim> historicalClaims = Arrays.asList(
            createTestClaim("John Doe", new BigDecimal("500.00"), "AUTO"),
            createTestClaim("John Doe", new BigDecimal("750.00"), "HEALTH"),
            createTestClaim("Alice Brown", new BigDecimal("600.00"), "AUTO")
        );
        
        when(claimRepository.findAll()).thenReturn(historicalClaims);

        FraudScoreResponse response = fraudScoringService.scoreClaim(newClaim);

        assertTrue(response.getFraudScore().compareTo(new BigDecimal("0.20")) > 0);
    }

    @Test
    void testScoringDisabled() {
        ReflectionTestUtils.setField(fraudScoringService, "scoringEnabled", false);

        Claim claim = createTestClaim("John Doe", new BigDecimal("1000.00"), "AUTO");

        FraudScoreResponse response = fraudScoringService.scoreClaim(claim);

        assertEquals(BigDecimal.ZERO, response.getFraudScore());
        assertEquals("LOW", response.getRiskLevel());
        assertEquals("Fraud scoring is disabled", response.getExplanation());
    }

    @Test
    void testRiskLevelClassification() {
        Claim claim = createTestClaim("John Doe", new BigDecimal("1000.00"), "AUTO");
        when(claimRepository.findAll()).thenReturn(Arrays.asList());

        FraudScoreResponse response = fraudScoringService.scoreClaim(claim);
        assertEquals("LOW", response.getRiskLevel());
    }

    private Claim createTestClaim(String name, BigDecimal amount, String type) {
        Claim claim = new Claim();
        claim.setId(UUID.randomUUID());
        claim.setClaimantName(name);
        claim.setClaimAmount(amount);
        claim.setClaimType(type);
        claim.setStatus("NEW");
        claim.setCreatedAt(LocalDateTime.now());
        return claim;
    }
}
