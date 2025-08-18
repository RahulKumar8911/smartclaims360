package com.smartclaims360.smartclaims360.service;

import com.smartclaims360.smartclaims360.dto.DuplicateDetectionResponse;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import com.smartclaims360.smartclaims360.repository.ClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuplicateDetectionServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private DuplicateDetectionService duplicateDetectionService;

    @Test
    void testFindAllDuplicatesWithNoDuplicates() {
        List<Claim> claims = Arrays.asList(
            createTestClaim("John", "Doe", LocalDate.of(1985, 3, 15)),
            createTestClaim("Jane", "Smith", LocalDate.of(1990, 7, 22)),
            createTestClaim("Bob", "Johnson", LocalDate.of(1978, 11, 8))
        );
        
        when(claimRepository.findAll()).thenReturn(claims);

        List<DuplicateDetectionResponse> duplicates = duplicateDetectionService.findAllDuplicates();

        assertTrue(duplicates.isEmpty());
    }

    @Test
    void testFindAllDuplicatesWithDuplicates() {
        List<Claim> claims = Arrays.asList(
            createTestClaim("John", "Doe", LocalDate.of(1985, 3, 15)),
            createTestClaim("John", "Doe", LocalDate.of(1985, 3, 15)),
            createTestClaim("Jane", "Smith", LocalDate.of(1990, 7, 22)),
            createTestClaim("Jane", "Smith", LocalDate.of(1990, 7, 22)),
            createTestClaim("Jane", "Smith", LocalDate.of(1990, 7, 22)),
            createTestClaim("Bob", "Johnson", LocalDate.of(1978, 11, 8))
        );
        
        when(claimRepository.findAll()).thenReturn(claims);

        List<DuplicateDetectionResponse> duplicates = duplicateDetectionService.findAllDuplicates();

        assertEquals(2, duplicates.size());
        
        DuplicateDetectionResponse johnDuplicates = duplicates.stream()
            .filter(d -> d.getFirstName().equals("John"))
            .findFirst()
            .orElse(null);
        assertNotNull(johnDuplicates);
        assertEquals("John", johnDuplicates.getFirstName());
        assertEquals("Doe", johnDuplicates.getLastName());
        assertEquals(LocalDate.of(1985, 3, 15), johnDuplicates.getDateOfBirth());
        assertEquals(2, johnDuplicates.getDuplicateCount());
        assertEquals(2, johnDuplicates.getDuplicateClaims().size());

        DuplicateDetectionResponse janeDuplicates = duplicates.stream()
            .filter(d -> d.getFirstName().equals("Jane"))
            .findFirst()
            .orElse(null);
        assertNotNull(janeDuplicates);
        assertEquals("Jane", janeDuplicates.getFirstName());
        assertEquals("Smith", janeDuplicates.getLastName());
        assertEquals(LocalDate.of(1990, 7, 22), janeDuplicates.getDateOfBirth());
        assertEquals(3, janeDuplicates.getDuplicateCount());
        assertEquals(3, janeDuplicates.getDuplicateClaims().size());
    }

    @Test
    void testFindAllDuplicatesCaseInsensitive() {
        List<Claim> claims = Arrays.asList(
            createTestClaim("John", "Doe", LocalDate.of(1985, 3, 15)),
            createTestClaim("JOHN", "DOE", LocalDate.of(1985, 3, 15)),
            createTestClaim("john", "doe", LocalDate.of(1985, 3, 15))
        );
        
        when(claimRepository.findAll()).thenReturn(claims);

        List<DuplicateDetectionResponse> duplicates = duplicateDetectionService.findAllDuplicates();

        assertEquals(1, duplicates.size());
        assertEquals(3, duplicates.get(0).getDuplicateCount());
    }

    @Test
    void testFindDuplicatesForProfile() {
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1985, 3, 15);
        
        List<Claim> expectedClaims = Arrays.asList(
            createTestClaim(firstName, lastName, dateOfBirth),
            createTestClaim(firstName, lastName, dateOfBirth)
        );
        
        when(claimRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth(
            firstName, lastName, dateOfBirth)).thenReturn(expectedClaims);

        List<Claim> result = duplicateDetectionService.findDuplicatesForProfile(firstName, lastName, dateOfBirth);

        assertEquals(2, result.size());
        assertEquals(expectedClaims, result);
    }

    @Test
    void testFindDuplicatesForProfileNoMatches() {
        String firstName = "NonExistent";
        String lastName = "Person";
        LocalDate dateOfBirth = LocalDate.of(2000, 1, 1);
        
        when(claimRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth(
            firstName, lastName, dateOfBirth)).thenReturn(Arrays.asList());

        List<Claim> result = duplicateDetectionService.findDuplicatesForProfile(firstName, lastName, dateOfBirth);

        assertTrue(result.isEmpty());
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
