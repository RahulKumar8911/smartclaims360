package com.smartclaims360.smartclaims360.repository;

import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DuplicateDetectionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    void testFindByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth() {
        Claim claim1 = createTestClaim("John", "Doe", LocalDate.of(1985, 3, 15));
        Claim claim2 = createTestClaim("JOHN", "DOE", LocalDate.of(1985, 3, 15));
        Claim claim3 = createTestClaim("jane", "smith", LocalDate.of(1990, 7, 22));
        
        entityManager.persistAndFlush(claim1);
        entityManager.persistAndFlush(claim2);
        entityManager.persistAndFlush(claim3);

        List<Claim> duplicates = claimRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth(
            "john", "doe", LocalDate.of(1985, 3, 15));

        assertEquals(2, duplicates.size());
        assertTrue(duplicates.stream().allMatch(c -> 
            c.getFirstName().toLowerCase().equals("john") && 
            c.getLastName().toLowerCase().equals("doe") &&
            c.getDateOfBirth().equals(LocalDate.of(1985, 3, 15))));
    }

    @Test
    void testFindByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirthNoMatches() {
        Claim claim1 = createTestClaim("John", "Doe", LocalDate.of(1985, 3, 15));
        entityManager.persistAndFlush(claim1);

        List<Claim> duplicates = claimRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth(
            "Jane", "Smith", LocalDate.of(1990, 7, 22));

        assertTrue(duplicates.isEmpty());
    }

    private Claim createTestClaim(String firstName, String lastName, LocalDate dateOfBirth) {
        Claim claim = new Claim();
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
