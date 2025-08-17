package com.smartclaims360.smartclaims360.exception;

import java.util.UUID;

public class ClaimNotFoundException extends RuntimeException {
    
    public ClaimNotFoundException(UUID id) {
        super("Claim not found with id: " + id);
    }
}
