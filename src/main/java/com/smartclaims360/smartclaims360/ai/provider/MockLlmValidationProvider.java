package com.smartclaims360.smartclaims360.ai.provider;

import com.smartclaims360.smartclaims360.entity.Claim;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class MockLlmValidationProvider implements LlmValidationProvider {

    @Override
    public List<String> getValidationHints(Claim claim) {
        List<String> hints = new ArrayList<>();
        
        if (claim.getClaimAmount().compareTo(new BigDecimal("10000")) > 0) {
            hints.add("High claim amount detected - consider additional documentation");
        }
        
        if (claim.getClaimantName().toLowerCase().contains("test")) {
            hints.add("Test claimant name detected - verify authenticity");
        }
        
        if ("LIFE".equals(claim.getClaimType())) {
            hints.add("Life insurance claim requires death certificate verification");
        }
        
        if (claim.getClaimantName().split(" ").length < 2) {
            hints.add("Single name provided - consider requesting full legal name");
        }
        
        return hints;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
