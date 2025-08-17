package com.smartclaims360.smartclaims360.repository;

import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ClaimRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    void testFindByStatus() {
        Claim claim1 = createTestClaim("John Doe", ClaimType.AUTO, ClaimStatus.NEW);
        Claim claim2 = createTestClaim("Jane Smith", ClaimType.HEALTH, ClaimStatus.REVIEW);
        Claim claim3 = createTestClaim("Bob Johnson", ClaimType.PROPERTY, ClaimStatus.NEW);

        entityManager.persistAndFlush(claim1);
        entityManager.persistAndFlush(claim2);
        entityManager.persistAndFlush(claim3);

        List<Claim> newClaims = claimRepository.findByStatus(ClaimStatus.NEW);
        assertThat(newClaims).hasSize(2);
        assertThat(newClaims).extracting(Claim::getClaimantName)
                .containsExactlyInAnyOrder("John Doe", "Bob Johnson");

        List<Claim> reviewClaims = claimRepository.findByStatus(ClaimStatus.REVIEW);
        assertThat(reviewClaims).hasSize(1);
        assertThat(reviewClaims.get(0).getClaimantName()).isEqualTo("Jane Smith");
    }

    @Test
    void testFindByClaimType() {
        Claim claim1 = createTestClaim("John Doe", ClaimType.AUTO, ClaimStatus.NEW);
        Claim claim2 = createTestClaim("Jane Smith", ClaimType.HEALTH, ClaimStatus.REVIEW);
        Claim claim3 = createTestClaim("Bob Johnson", ClaimType.AUTO, ClaimStatus.APPROVED);

        entityManager.persistAndFlush(claim1);
        entityManager.persistAndFlush(claim2);
        entityManager.persistAndFlush(claim3);

        List<Claim> autoClaims = claimRepository.findByClaimType(ClaimType.AUTO);
        assertThat(autoClaims).hasSize(2);
        assertThat(autoClaims).extracting(Claim::getClaimantName)
                .containsExactlyInAnyOrder("John Doe", "Bob Johnson");

        List<Claim> healthClaims = claimRepository.findByClaimType(ClaimType.HEALTH);
        assertThat(healthClaims).hasSize(1);
        assertThat(healthClaims.get(0).getClaimantName()).isEqualTo("Jane Smith");
    }

    @Test
    void testFindByStatusAndClaimType() {
        Claim claim1 = createTestClaim("John Doe", ClaimType.AUTO, ClaimStatus.NEW);
        Claim claim2 = createTestClaim("Jane Smith", ClaimType.HEALTH, ClaimStatus.NEW);
        Claim claim3 = createTestClaim("Bob Johnson", ClaimType.AUTO, ClaimStatus.REVIEW);

        entityManager.persistAndFlush(claim1);
        entityManager.persistAndFlush(claim2);
        entityManager.persistAndFlush(claim3);

        List<Claim> newAutoClaims = claimRepository.findByStatusAndClaimType(ClaimStatus.NEW, ClaimType.AUTO);
        assertThat(newAutoClaims).hasSize(1);
        assertThat(newAutoClaims.get(0).getClaimantName()).isEqualTo("John Doe");

        List<Claim> reviewAutoClaims = claimRepository.findByStatusAndClaimType(ClaimStatus.REVIEW, ClaimType.AUTO);
        assertThat(reviewAutoClaims).hasSize(1);
        assertThat(reviewAutoClaims.get(0).getClaimantName()).isEqualTo("Bob Johnson");

        List<Claim> approvedHealthClaims = claimRepository.findByStatusAndClaimType(ClaimStatus.APPROVED, ClaimType.HEALTH);
        assertThat(approvedHealthClaims).isEmpty();
    }

    @Test
    void testEnumPersistence() {
        Claim claim = createTestClaim("Test User", ClaimType.PROPERTY, ClaimStatus.REJECTED);
        Claim savedClaim = entityManager.persistAndFlush(claim);

        Claim foundClaim = claimRepository.findById(savedClaim.getId()).orElse(null);
        assertThat(foundClaim).isNotNull();
        assertThat(foundClaim.getClaimType()).isEqualTo(ClaimType.PROPERTY);
        assertThat(foundClaim.getStatus()).isEqualTo(ClaimStatus.REJECTED);
    }

    private Claim createTestClaim(String claimantName, ClaimType claimType, ClaimStatus status) {
        Claim claim = new Claim();
        claim.setClaimantName(claimantName);
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setClaimType(claimType);
        claim.setStatus(status);
        return claim;
    }
}
