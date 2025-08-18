package com.smartclaims360.smartclaims360.service;

import com.smartclaims360.smartclaims360.dto.DuplicateDetectionResponse;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DuplicateDetectionService {

    @Autowired
    private ClaimRepository claimRepository;

    public List<DuplicateDetectionResponse> findAllDuplicates() {
        List<Claim> allClaims = claimRepository.findAll();
        
        Map<String, List<Claim>> groupedClaims = allClaims.stream()
                .collect(Collectors.groupingBy(claim -> 
                    claim.getFirstName().toLowerCase() + "|" + 
                    claim.getLastName().toLowerCase() + "|" + 
                    claim.getDateOfBirth().toString()));
        
        List<DuplicateDetectionResponse> duplicates = new ArrayList<>();
        for (Map.Entry<String, List<Claim>> entry : groupedClaims.entrySet()) {
            List<Claim> claims = entry.getValue();
            if (claims.size() > 1) {
                Claim firstClaim = claims.get(0);
                duplicates.add(new DuplicateDetectionResponse(
                    firstClaim.getFirstName(),
                    firstClaim.getLastName(),
                    firstClaim.getDateOfBirth(),
                    claims.size(),
                    claims
                ));
            }
        }
        
        return duplicates;
    }

    public List<Claim> findDuplicatesForProfile(String firstName, String lastName, LocalDate dateOfBirth) {
        return claimRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth(
            firstName, lastName, dateOfBirth);
    }
}
