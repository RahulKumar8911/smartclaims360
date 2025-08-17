package com.smartclaims360.smartclaims360.ai.controller;

import com.smartclaims360.smartclaims360.ai.dto.FraudScoreResponse;
import com.smartclaims360.smartclaims360.ai.dto.RoutingSuggestion;
import com.smartclaims360.smartclaims360.ai.dto.ValidationResponse;
import com.smartclaims360.smartclaims360.ai.service.AiValidationService;
import com.smartclaims360.smartclaims360.ai.service.FraudScoringService;
import com.smartclaims360.smartclaims360.ai.service.RoutingService;
import com.smartclaims360.smartclaims360.ai.service.SummarizationService;
import com.smartclaims360.smartclaims360.entity.Claim;
import com.smartclaims360.smartclaims360.exception.ClaimNotFoundException;
import com.smartclaims360.smartclaims360.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/claims")
public class AiController {

    @Autowired
    private AiValidationService aiValidationService;

    @Autowired
    private FraudScoringService fraudScoringService;

    @Autowired
    private SummarizationService summarizationService;

    @Autowired
    private RoutingService routingService;

    @Autowired
    private ClaimService claimService;

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateClaim(@RequestBody Claim claim) {
        ValidationResponse response = aiValidationService.validateClaim(claim);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/score")
    public ResponseEntity<FraudScoreResponse> scoreClaim(@RequestBody Claim claim) {
        FraudScoreResponse response = fraudScoringService.scoreClaim(claim);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<String> getClaimSummary(@PathVariable UUID id) {
        Claim claim = claimService.getClaimById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        
        String summary = summarizationService.summarize(claim);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{id}/route")
    public ResponseEntity<RoutingSuggestion> getRoutingSuggestion(@PathVariable UUID id) {
        Claim claim = claimService.getClaimById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        
        RoutingSuggestion suggestion = routingService.suggest(claim);
        return ResponseEntity.ok(suggestion);
    }

    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<String> handleClaimNotFound(ClaimNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
