package com.smartclaims360.smartclaims360.ai.provider;

import com.smartclaims360.smartclaims360.entity.Claim;

import java.util.List;

public interface LlmValidationProvider {
    
    List<String> getValidationHints(Claim claim);
    
    boolean isEnabled();
}
