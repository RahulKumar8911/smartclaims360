package com.smartclaims360.smartclaims360.service;

import com.smartclaims360.smartclaims360.dto.ClaimRequest;
import com.smartclaims360.smartclaims360.entity.Claim;
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
        claim.setStatus("NEW");
        
        return claimRepository.save(claim);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Optional<Claim> getClaimById(UUID id) {
        return claimRepository.findById(id);
    }
}
