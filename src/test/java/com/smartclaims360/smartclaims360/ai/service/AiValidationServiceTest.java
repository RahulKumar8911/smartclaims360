package com.smartclaims360.smartclaims360.ai.service;

import com.smartclaims360.smartclaims360.ai.dto.ValidationResponse;
import com.smartclaims360.smartclaims360.ai.provider.LlmValidationProvider;
import com.smartclaims360.smartclaims360.entity.Claim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiValidationServiceTest {

    @Mock
    private LlmValidationProvider llmValidationProvider;

    @InjectMocks
    private AiValidationService aiValidationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiValidationService, "validationEnabled", true);
    }

    @Test
    void testValidateValidClaim() {
        Claim claim = new Claim();
        claim.setClaimantName("John Doe");
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setClaimType("AUTO");

        List<String> mockHints = Arrays.asList("Consider additional documentation");
        when(llmValidationProvider.isEnabled()).thenReturn(true);
        when(llmValidationProvider.getValidationHints(claim)).thenReturn(mockHints);

        ValidationResponse response = aiValidationService.validateClaim(claim);

        assertTrue(response.isValid());
        assertTrue(response.getReasons().isEmpty());
        assertEquals(mockHints, response.getLlmHints());
    }

    @Test
    void testValidateInvalidClaimBlankName() {
        Claim claim = new Claim();
        claim.setClaimantName("");
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setClaimType("AUTO");

        when(llmValidationProvider.isEnabled()).thenReturn(true);
        when(llmValidationProvider.getValidationHints(claim)).thenReturn(Arrays.asList());

        ValidationResponse response = aiValidationService.validateClaim(claim);

        assertFalse(response.isValid());
        assertTrue(response.getReasons().contains("Claimant name cannot be blank"));
    }

    @Test
    void testValidateInvalidClaimNegativeAmount() {
        Claim claim = new Claim();
        claim.setClaimantName("John Doe");
        claim.setClaimAmount(new BigDecimal("-100.00"));
        claim.setClaimType("AUTO");

        when(llmValidationProvider.isEnabled()).thenReturn(true);
        when(llmValidationProvider.getValidationHints(claim)).thenReturn(Arrays.asList());

        ValidationResponse response = aiValidationService.validateClaim(claim);

        assertFalse(response.isValid());
        assertTrue(response.getReasons().contains("Claim amount must be greater than 0"));
    }

    @Test
    void testValidateInvalidClaimType() {
        Claim claim = new Claim();
        claim.setClaimantName("John Doe");
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setClaimType("INVALID");

        when(llmValidationProvider.isEnabled()).thenReturn(true);
        when(llmValidationProvider.getValidationHints(claim)).thenReturn(Arrays.asList());

        ValidationResponse response = aiValidationService.validateClaim(claim);

        assertFalse(response.isValid());
        assertTrue(response.getReasons().contains("Claim type must be one of: AUTO, HEALTH, PROPERTY, LIFE"));
    }

    @Test
    void testValidationDisabled() {
        ReflectionTestUtils.setField(aiValidationService, "validationEnabled", false);

        Claim claim = new Claim();
        claim.setClaimantName("");
        claim.setClaimAmount(new BigDecimal("-100.00"));
        claim.setClaimType("INVALID");

        ValidationResponse response = aiValidationService.validateClaim(claim);

        assertTrue(response.isValid());
        assertTrue(response.getReasons().isEmpty());
        assertTrue(response.getLlmHints().isEmpty());
    }
}
