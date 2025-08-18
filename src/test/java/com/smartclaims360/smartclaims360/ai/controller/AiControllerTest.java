package com.smartclaims360.smartclaims360.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclaims360.smartclaims360.ai.dto.FraudScoreResponse;
import com.smartclaims360.smartclaims360.ai.dto.RoutingSuggestion;
import com.smartclaims360.smartclaims360.ai.dto.ValidationResponse;
import com.smartclaims360.smartclaims360.ai.service.AiValidationService;
import com.smartclaims360.smartclaims360.ai.service.FraudScoringService;
import com.smartclaims360.smartclaims360.ai.service.RoutingService;
import com.smartclaims360.smartclaims360.ai.service.SummarizationService;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import com.smartclaims360.smartclaims360.service.ClaimService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiController.class)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiValidationService aiValidationService;

    @MockBean
    private FraudScoringService fraudScoringService;

    @MockBean
    private SummarizationService summarizationService;

    @MockBean
    private RoutingService routingService;

    @MockBean
    private ClaimService claimService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testValidateClaim() throws Exception {
        Claim claim = createTestClaim();
        ValidationResponse response = new ValidationResponse(true, Arrays.asList(), Arrays.asList("Consider additional documentation"));
        
        when(aiValidationService.validateClaim(any(Claim.class))).thenReturn(response);

        mockMvc.perform(post("/claims/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claim)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.reasons").isEmpty())
                .andExpect(jsonPath("$.llmHints[0]").value("Consider additional documentation"));
    }

    @Test
    void testScoreClaim() throws Exception {
        Claim claim = createTestClaim();
        FraudScoreResponse response = new FraudScoreResponse(new BigDecimal("0.25"), "LOW", "Low fraud risk");
        
        when(fraudScoringService.scoreClaim(any(Claim.class))).thenReturn(response);

        mockMvc.perform(post("/claims/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claim)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fraudScore").value(0.25))
                .andExpect(jsonPath("$.riskLevel").value("LOW"))
                .andExpect(jsonPath("$.explanation").value("Low fraud risk"));
    }

    @Test
    void testGetClaimSummary() throws Exception {
        UUID claimId = UUID.randomUUID();
        Claim claim = createTestClaim();
        claim.setId(claimId);
        
        when(claimService.getClaimById(claimId)).thenReturn(Optional.of(claim));
        when(summarizationService.summarize(claim)).thenReturn("Test summary");

        mockMvc.perform(get("/claims/" + claimId + "/summary"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test summary"));
    }

    @Test
    void testGetRoutingSuggestion() throws Exception {
        UUID claimId = UUID.randomUUID();
        Claim claim = createTestClaim();
        claim.setId(claimId);
        RoutingSuggestion suggestion = new RoutingSuggestion("AUTO", "Standard processing");
        
        when(claimService.getClaimById(claimId)).thenReturn(Optional.of(claim));
        when(routingService.suggest(claim)).thenReturn(suggestion);

        mockMvc.perform(get("/claims/" + claimId + "/route"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queue").value("AUTO"))
                .andExpect(jsonPath("$.reason").value("Standard processing"));
    }

    @Test
    void testClaimNotFound() throws Exception {
        UUID claimId = UUID.randomUUID();
        
        when(claimService.getClaimById(claimId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/claims/" + claimId + "/summary"))
                .andExpect(status().isNotFound());
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
