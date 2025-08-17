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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/claims")
@Tag(name = "AI-Assisted Features", description = "AI-powered claim validation, fraud detection, summarization, and routing")
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
    @Operation(
        summary = "Validate Claim with AI",
        description = "Performs rule-based validation and provides AI-powered hints for claim improvement. Validates claimant name, amount, and claim type constraints."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Validation completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ValidationResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Valid Claim",
                        value = """
                            {
                                "valid": true,
                                "reasons": [],
                                "llmHints": [
                                    "Consider adding additional documentation for faster processing",
                                    "Claim amount is within normal range for AUTO claims"
                                ]
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Invalid Claim",
                        value = """
                            {
                                "valid": false,
                                "reasons": [
                                    "Claimant name cannot be blank",
                                    "Claim amount must be greater than 0"
                                ],
                                "llmHints": [
                                    "Ensure all required fields are properly filled",
                                    "Verify claim amount reflects actual damages"
                                ]
                            }
                            """
                    )
                }
            )
        )
    })
    public ResponseEntity<ValidationResponse> validateClaim(
        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Claim data to validate",
            content = @Content(
                schema = @Schema(implementation = Claim.class),
                examples = @ExampleObject(value = """
                    {
                        "claimantName": "John Doe",
                        "claimAmount": 1500.00,
                        "claimType": "AUTO"
                    }
                    """)
            )
        )
        Claim claim) {
        ValidationResponse response = aiValidationService.validateClaim(claim);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/score")
    @Operation(
        summary = "Calculate Fraud Risk Score",
        description = "Analyzes claim data using statistical algorithms to calculate fraud risk score (0.0-1.0). Uses z-score analysis, claim type frequency, and pattern detection."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Fraud scoring completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FraudScoreResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Low Risk Claim",
                        value = """
                            {
                                "fraudScore": 0.25,
                                "riskLevel": "LOW",
                                "explanation": "Claim amount within normal range. Standard claim type frequency. No suspicious patterns detected."
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "High Risk Claim",
                        value = """
                            {
                                "fraudScore": 0.85,
                                "riskLevel": "HIGH",
                                "explanation": "Claim amount significantly above average (z-score: 2.8). Repeated claimant name detected. Unusual claim type frequency."
                            }
                            """
                    )
                }
            )
        )
    })
    public ResponseEntity<FraudScoreResponse> scoreClaim(
        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Claim data to analyze for fraud risk",
            content = @Content(
                schema = @Schema(implementation = Claim.class),
                examples = @ExampleObject(value = """
                    {
                        "claimantName": "John Doe",
                        "claimAmount": 15000.00,
                        "claimType": "AUTO"
                    }
                    """)
            )
        )
        Claim claim) {
        FraudScoreResponse response = fraudScoringService.scoreClaim(claim);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/summary")
    @Operation(
        summary = "Generate Claim Summary",
        description = "Creates a comprehensive summary of the claim including key details, validation status, fraud risk analysis, and processing recommendations."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Claim summary generated successfully",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(value = """
                    CLAIM SUMMARY
                    =============
                    Claim ID: 123e4567-e89b-12d3-a456-426614174000
                    Claimant: John Doe
                    Amount: $1,500.00
                    Type: AUTO
                    Status: NEW
                    Created: 2025-08-17T14:30:00
                    
                    VALIDATION STATUS
                    ================
                    Status: VALID
                    AI Recommendations: Consider adding additional documentation for faster processing
                    
                    FRAUD RISK ANALYSIS
                    ==================
                    Fraud Score: 0.25 (LOW RISK)
                    Risk Analysis: Claim amount within normal range. Standard claim type frequency.
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Claim not found with the provided ID",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "timestamp": "2025-08-17T14:30:00.000+00:00",
                        "status": 404,
                        "error": "Not Found",
                        "message": "Claim not found with id: 123e4567-e89b-12d3-a456-426614174000",
                        "path": "/claims/123e4567-e89b-12d3-a456-426614174000/summary"
                    }
                    """)
            )
        )
    })
    public ResponseEntity<String> getClaimSummary(@PathVariable UUID id) {
        Claim claim = claimService.getClaimById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        
        String summary = summarizationService.summarize(claim);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{id}/route")
    @Operation(
        summary = "Get Routing Suggestion",
        description = "Provides intelligent routing recommendations based on claim type, fraud score, and validation results. Routes high-risk claims to manual review."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Routing suggestion generated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RoutingSuggestion.class),
                examples = {
                    @ExampleObject(
                        name = "Standard Processing",
                        value = """
                            {
                                "queue": "AUTO",
                                "reason": "Standard AUTO claim with low fraud risk (0.25). Route to automated processing."
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Manual Review Required",
                        value = """
                            {
                                "queue": "MANUAL_REVIEW",
                                "reason": "High fraud risk detected (0.85). Requires manual investigation and approval."
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Claim not found with the provided ID",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "timestamp": "2025-08-17T14:30:00.000+00:00",
                        "status": 404,
                        "error": "Not Found",
                        "message": "Claim not found with id: 123e4567-e89b-12d3-a456-426614174000",
                        "path": "/claims/123e4567-e89b-12d3-a456-426614174000/route"
                    }
                    """)
            )
        )
    })
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
