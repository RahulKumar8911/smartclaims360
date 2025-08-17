package com.smartclaims360.smartclaims360.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclaims360.smartclaims360.dto.ClaimRequest;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.service.ClaimService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClaimController.class)
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaimService claimService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("SmartClaims360 API is running"));
    }

    @Test
    void testCreateClaim() throws Exception {
        ClaimRequest claimRequest = new ClaimRequest();
        claimRequest.setClaimantName("John Doe");
        claimRequest.setClaimAmount(new BigDecimal("1000.00"));
        claimRequest.setClaimType("AUTO");

        Claim createdClaim = new Claim();
        createdClaim.setId(UUID.randomUUID());
        createdClaim.setClaimantName("John Doe");
        createdClaim.setClaimAmount(new BigDecimal("1000.00"));
        createdClaim.setClaimType("AUTO");
        createdClaim.setStatus("NEW");
        createdClaim.setCreatedAt(LocalDateTime.now());

        when(claimService.createClaim(any(ClaimRequest.class))).thenReturn(createdClaim);

        mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimantName").value("John Doe"))
                .andExpect(jsonPath("$.claimAmount").value(1000.00))
                .andExpect(jsonPath("$.claimType").value("AUTO"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void testCreateClaimWithInvalidData() throws Exception {
        ClaimRequest claimRequest = new ClaimRequest();
        claimRequest.setClaimantName("");
        claimRequest.setClaimAmount(new BigDecimal("-100.00"));
        claimRequest.setClaimType("AUTO");

        mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllClaims() throws Exception {
        Claim claim1 = new Claim();
        claim1.setId(UUID.randomUUID());
        claim1.setClaimantName("John Doe");
        claim1.setClaimAmount(new BigDecimal("1000.00"));
        claim1.setClaimType("AUTO");
        claim1.setStatus("NEW");

        Claim claim2 = new Claim();
        claim2.setId(UUID.randomUUID());
        claim2.setClaimantName("Jane Smith");
        claim2.setClaimAmount(new BigDecimal("2000.00"));
        claim2.setClaimType("HOME");
        claim2.setStatus("NEW");

        when(claimService.getAllClaims()).thenReturn(Arrays.asList(claim1, claim2));

        mockMvc.perform(get("/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].claimantName").value("John Doe"))
                .andExpect(jsonPath("$[1].claimantName").value("Jane Smith"));
    }

    @Test
    void testGetClaimById() throws Exception {
        UUID claimId = UUID.randomUUID();
        Claim claim = new Claim();
        claim.setId(claimId);
        claim.setClaimantName("John Doe");
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setClaimType("AUTO");
        claim.setStatus("NEW");

        when(claimService.getClaimById(claimId)).thenReturn(Optional.of(claim));

        mockMvc.perform(get("/claims/" + claimId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(claimId.toString()))
                .andExpect(jsonPath("$.claimantName").value("John Doe"));
    }

    @Test
    void testGetClaimByIdNotFound() throws Exception {
        UUID claimId = UUID.randomUUID();
        when(claimService.getClaimById(claimId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/claims/" + claimId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Claim not found with id: " + claimId));
    }
}
