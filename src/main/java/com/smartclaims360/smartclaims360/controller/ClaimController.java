package com.smartclaims360.smartclaims360.controller;

import com.smartclaims360.smartclaims360.dto.ClaimRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Claim Management", description = "Traditional CRUD operations for claim management")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @GetMapping("/health")
    @Operation(
        summary = "Health Check",
        description = "Returns the health status of the SmartClaims360 API to verify the service is running properly"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Service is healthy and running",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(value = "SmartClaims360 API is running")
            )
        )
    })
    public String health() {
        return "SmartClaims360 API is running";
    }

    @PostMapping("/claims")
    @Operation(
        summary = "Create New Claim",
        description = "Creates a new insurance claim with validation. Supports AUTO, HEALTH, PROPERTY, and LIFE claim types."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Claim created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Claim.class),
                examples = @ExampleObject(value = """
                    {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "claimantName": "John Doe",
                        "claimAmount": 1500.00,
                        "claimType": "AUTO",
                        "status": "NEW",
                        "createdAt": "2025-08-17T14:30:00",
                        "fraudScore": null
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid claim data provided",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "timestamp": "2025-08-17T14:30:00.000+00:00",
                        "status": 400,
                        "error": "Bad Request",
                        "message": "Validation failed",
                        "path": "/claims"
                    }
                    """)
            )
        )
    })
    public ResponseEntity<Claim> createClaim(
        @Valid @RequestBody 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Claim creation request with claimant details",
            content = @Content(
                schema = @Schema(implementation = ClaimRequest.class),
                examples = @ExampleObject(value = """
                    {
                        "claimantName": "John Doe",
                        "claimAmount": 1500.00,
                        "claimType": "AUTO"
                    }
                    """)
            )
        )
        ClaimRequest claimRequest) {
        Claim createdClaim = claimService.createClaim(claimRequest);
        return new ResponseEntity<>(createdClaim, HttpStatus.CREATED);
    }

    @GetMapping("/claims")
    @Operation(
        summary = "List All Claims",
        description = "Retrieves a list of all claims in the system with their current status and fraud scores"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Claims retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Claim.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "claimantName": "John Doe",
                            "claimAmount": 1500.00,
                            "claimType": "AUTO",
                            "status": "NEW",
                            "createdAt": "2025-08-17T14:30:00",
                            "fraudScore": 0.25
                        },
                        {
                            "id": "987fcdeb-51a2-43d1-9c4f-123456789abc",
                            "claimantName": "Jane Smith",
                            "claimAmount": 5000.00,
                            "claimType": "HEALTH",
                            "status": "PROCESSING",
                            "createdAt": "2025-08-17T13:15:00",
                            "fraudScore": 0.75
                        }
                    ]
                    """)
            )
        )
    })
    public ResponseEntity<List<Claim>> getAllClaims() {
        List<Claim> claims = claimService.getAllClaims();
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/claims/{id}")
    @Operation(
        summary = "Get Claim by ID",
        description = "Retrieves a specific claim by its unique identifier including fraud score and processing status"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Claim found and returned successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Claim.class),
                examples = @ExampleObject(value = """
                    {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "claimantName": "John Doe",
                        "claimAmount": 1500.00,
                        "claimType": "AUTO",
                        "status": "NEW",
                        "createdAt": "2025-08-17T14:30:00",
                        "fraudScore": 0.25
                    }
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
                        "path": "/claims/123e4567-e89b-12d3-a456-426614174000"
                    }
                    """)
            )
        )
    })
    public ResponseEntity<Claim> getClaimById(@PathVariable UUID id) {
        return claimService.getClaimById(id)
                .map(claim -> ResponseEntity.ok(claim))
                .orElseThrow(() -> new ClaimNotFoundException(id));
    }

    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<String> handleClaimNotFound(ClaimNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
