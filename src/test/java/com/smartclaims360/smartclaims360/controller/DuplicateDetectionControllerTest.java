package com.smartclaims360.smartclaims360.controller;

import com.smartclaims360.smartclaims360.dto.DuplicateDetectionResponse;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import com.smartclaims360.smartclaims360.service.ClaimService;
import com.smartclaims360.smartclaims360.service.DuplicateDetectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClaimController.class)
class DuplicateDetectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DuplicateDetectionService duplicateDetectionService;
    
    @MockBean
    private ClaimService claimService;

    @Test
    void testFindDuplicateClaimsEndpoint() throws Exception {
        Claim claim1 = createTestClaim("John", "Doe", LocalDate.of(1985, 3, 15));
        Claim claim2 = createTestClaim("John", "Doe", LocalDate.of(1985, 3, 15));
        
        DuplicateDetectionResponse response = new DuplicateDetectionResponse(
            "John", "Doe", LocalDate.of(1985, 3, 15), 2, Arrays.asList(claim1, claim2)
        );
        
        when(duplicateDetectionService.findAllDuplicates()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/claims/duplicates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].dateOfBirth").value("1985-03-15"))
                .andExpect(jsonPath("$[0].duplicateCount").value(2))
                .andExpect(jsonPath("$[0].duplicateClaims").isArray())
                .andExpect(jsonPath("$[0].duplicateClaims.length()").value(2));
    }

    @Test
    void testFindDuplicateClaimsNoDuplicates() throws Exception {
        when(duplicateDetectionService.findAllDuplicates()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/claims/duplicates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private Claim createTestClaim(String firstName, String lastName, LocalDate dateOfBirth) {
        Claim claim = new Claim();
        claim.setId(UUID.randomUUID());
        claim.setClaimantName(firstName + " " + lastName);
        claim.setFirstName(firstName);
        claim.setLastName(lastName);
        claim.setDateOfBirth(dateOfBirth);
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setClaimType(ClaimType.AUTO);
        claim.setStatus(ClaimStatus.NEW);
        claim.setCreatedAt(LocalDateTime.now());
        return claim;
    }
}
