package com.smartclaims360.smartclaims360.service;

import com.smartclaims360.smartclaims360.dto.ClaimRequest;
import com.smartclaims360.smartclaims360.dto.ClaimUpdateRequest;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.entity.ClaimStatus;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import com.smartclaims360.smartclaims360.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    public Claim createClaim(ClaimRequest claimRequest) {
        Claim claim = new Claim();
        claim.setClaimantName(claimRequest.getClaimantName());
        claim.setClaimAmount(claimRequest.getClaimAmount());
        claim.setClaimType(claimRequest.getClaimType());
        claim.setStatus(ClaimStatus.NEW);
        
        return claimRepository.save(claim);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Optional<Claim> getClaimById(UUID id) {
        return claimRepository.findById(id);
    }

    public Optional<Claim> updateClaim(UUID id, ClaimUpdateRequest updateRequest) {
        Optional<Claim> existingClaim = claimRepository.findById(id);
        if (existingClaim.isPresent()) {
            Claim claim = existingClaim.get();
            claim.setClaimantName(updateRequest.getClaimantName());
            claim.setClaimAmount(updateRequest.getClaimAmount());
            claim.setClaimType(updateRequest.getClaimType());
            if (updateRequest.getStatus() != null) {
                claim.setStatus(updateRequest.getStatus());
            }
            return Optional.of(claimRepository.save(claim));
        }
        return Optional.empty();
    }

    public boolean deleteClaim(UUID id) {
        if (claimRepository.existsById(id)) {
            claimRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Claim> searchClaims(ClaimStatus status, ClaimType claimType) {
        if (status != null && claimType != null) {
            return claimRepository.findByStatusAndClaimType(status, claimType);
        } else if (status != null) {
            return claimRepository.findByStatus(status);
        } else if (claimType != null) {
            return claimRepository.findByClaimType(claimType);
        } else {
            return claimRepository.findAll();
        }
    }
}
