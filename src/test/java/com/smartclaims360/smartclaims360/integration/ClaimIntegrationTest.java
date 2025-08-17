package com.smartclaims360.smartclaims360.integration;

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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClaimIntegrationTest {

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
    void testCreateAndRetrieveClaim() throws Exception {
        ClaimRequest claimRequest = new ClaimRequest();
        claimRequest.setClaimantName("Integration Test User");
        claimRequest.setClaimAmount(new BigDecimal("5000.00"));
        claimRequest.setClaimType(ClaimType.HEALTH);

        String response = mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimantName").value("Integration Test User"))
                .andExpect(jsonPath("$.claimAmount").value(5000.00))
                .andExpect(jsonPath("$.claimType").value("HEALTH"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Claim createdClaim = objectMapper.readValue(response, Claim.class);
        UUID claimId = createdClaim.getId();

        mockMvc.perform(get("/claims/" + claimId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(claimId.toString()))
                .andExpect(jsonPath("$.claimantName").value("Integration Test User"));

        mockMvc.perform(get("/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].claimantName").value("Integration Test User"));
    }

    @Test
    void testValidationErrors() throws Exception {
        ClaimRequest invalidRequest = new ClaimRequest();
        invalidRequest.setClaimantName("");
        invalidRequest.setClaimAmount(new BigDecimal("-100"));
        invalidRequest.setClaimType(null);

        mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testClaimNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        
        mockMvc.perform(get("/claims/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Claim not found with id: " + nonExistentId));
    }
}
