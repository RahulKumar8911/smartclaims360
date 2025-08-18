package com.smartclaims360.smartclaims360.repository;

import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    List<Claim> findByStatus(ClaimStatus status);
    List<Claim> findByClaimType(ClaimType claimType);
    List<Claim> findByStatusAndClaimType(ClaimStatus status, ClaimType claimType);
    List<Claim> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth(String firstName, String lastName, LocalDate dateOfBirth);
}
