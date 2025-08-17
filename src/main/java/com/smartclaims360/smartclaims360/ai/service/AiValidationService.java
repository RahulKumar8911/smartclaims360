package com.smartclaims360.smartclaims360.ai.service;

import com.smartclaims360.smartclaims360.ai.dto.ValidationResponse;
import com.smartclaims360.smartclaims360.ai.provider.LlmValidationProvider;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.entity.ClaimType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AiValidationService {

    @Autowired
    private LlmValidationProvider llmValidationProvider;

    @Value("${ai.validation.enabled:true}")
    private boolean validationEnabled;

    private static final List<ClaimType> VALID_CLAIM_TYPES = Arrays.asList(ClaimType.values());

    public ValidationResponse validateClaim(Claim claim) {
        if (!validationEnabled) {
            return new ValidationResponse(true, new ArrayList<>(), new ArrayList<>());
        }

        List<String> reasons = new ArrayList<>();
        boolean isValid = true;

        if (claim.getClaimantName() == null || claim.getClaimantName().trim().isEmpty()) {
            reasons.add("Claimant name cannot be blank");
            isValid = false;
        }

        if (claim.getClaimAmount() == null || claim.getClaimAmount().compareTo(BigDecimal.ZERO) <= 0) {
            reasons.add("Claim amount must be greater than 0");
            isValid = false;
        }

        if (claim.getClaimType() == null || !VALID_CLAIM_TYPES.contains(claim.getClaimType())) {
            reasons.add("Claim type must be one of: " + Arrays.toString(ClaimType.values()));
            isValid = false;
        }

        List<String> llmHints = new ArrayList<>();
        if (llmValidationProvider.isEnabled()) {
            llmHints = llmValidationProvider.getValidationHints(claim);
        }

        return new ValidationResponse(isValid, reasons, llmHints);
    }
}
