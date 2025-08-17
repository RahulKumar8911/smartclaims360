package com.smartclaims360.smartclaims360.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutingSuggestion {
    
    private String queue;
    private String reason;
}
