package com.smartclaims360.smartclaims360.ai.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclaims360.smartclaims360.dto.ClaimRequest;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import com.smartclaims360.smartclaims360.repository.ClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        claimRepository.deleteAll();
    }

    @Test
    void testValidateClaimEndpoint() throws Exception {
        Claim claim = new Claim();
        claim.setClaimantName("John Doe");
        claim.setFirstName("John");
        claim.setLastName("Doe");
        claim.setDateOfBirth(LocalDate.of(1985, 3, 15));
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setClaimType(ClaimType.AUTO);

        mockMvc.perform(post("/claims/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claim)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.reasons").isEmpty())
                .andExpect(jsonPath("$.llmHints").isArray());
    }

    @Test
    void testValidateInvalidClaim() throws Exception {
        Claim claim = new Claim();
        claim.setClaimantName("");
        claim.setFirstName("");
        claim.setLastName("");
        claim.setDateOfBirth(null);
        claim.setClaimAmount(new BigDecimal("-100.00"));
        claim.setClaimType(null);

        mockMvc.perform(post("/claims/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claim)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.reasons").isArray())
                .andExpect(jsonPath("$.reasons.length()").value(3));
    }

    @Test
    void testScoreClaimEndpoint() throws Exception {
        Claim claim = new Claim();
        claim.setClaimantName("John Doe");
        claim.setFirstName("John");
        claim.setLastName("Doe");
        claim.setDateOfBirth(LocalDate.of(1985, 3, 15));
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setClaimType(ClaimType.AUTO);

        mockMvc.perform(post("/claims/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claim)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fraudScore").exists())
                .andExpect(jsonPath("$.riskLevel").exists())
                .andExpect(jsonPath("$.explanation").exists());
    }

    @Test
    void testClaimSummaryEndpoint() throws Exception {
        ClaimRequest claimRequest = new ClaimRequest();
        claimRequest.setClaimantName("Integration Test User");
        claimRequest.setFirstName("John");
        claimRequest.setLastName("Doe");
        claimRequest.setDateOfBirth(LocalDate.of(1985, 3, 15));
        claimRequest.setClaimAmount(new BigDecimal("5000.00"));
        claimRequest.setClaimType(ClaimType.HEALTH);

        String response = mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Claim createdClaim = objectMapper.readValue(response, Claim.class);
        UUID claimId = createdClaim.getId();

        mockMvc.perform(get("/claims/" + claimId + "/summary"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("CLAIM SUMMARY")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Integration Test User")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("HEALTH")));
    }

    @Test
    void testRoutingSuggestionEndpoint() throws Exception {
        ClaimRequest claimRequest = new ClaimRequest();
        claimRequest.setClaimantName("Routing Test User");
        claimRequest.setFirstName("John");
        claimRequest.setLastName("Doe");
        claimRequest.setDateOfBirth(LocalDate.of(1985, 3, 15));
        claimRequest.setClaimAmount(new BigDecimal("2000.00"));
        claimRequest.setClaimType(ClaimType.PROPERTY);

        String response = mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Claim createdClaim = objectMapper.readValue(response, Claim.class);
        UUID claimId = createdClaim.getId();

        mockMvc.perform(get("/claims/" + claimId + "/route"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queue").exists())
                .andExpect(jsonPath("$.reason").exists());
    }

    @Test
    void testClaimNotFoundForSummary() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        
        mockMvc.perform(get("/claims/" + nonExistentId + "/summary"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testClaimNotFoundForRouting() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        
        mockMvc.perform(get("/claims/" + nonExistentId + "/route"))
                .andExpect(status().isNotFound());
    }
}
