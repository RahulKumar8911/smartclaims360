package com.smartclaims360.smartclaims360.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResponse {
    
    private boolean valid;
    private List<String> reasons;
    private List<String> llmHints;
}
