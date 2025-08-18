package com.smartclaims360.smartclaims360.dto;

import com.smartclaims360.smartclaims360.entity.Claim;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing duplicate claims detection results")
public class DuplicateDetectionResponse {
    
    @Schema(description = "First name used for duplicate detection", example = "John")
    private String firstName;
    
    @Schema(description = "Last name used for duplicate detection", example = "Doe")
    private String lastName;
    
    @Schema(description = "Date of birth used for duplicate detection", example = "1985-03-15")
    private LocalDate dateOfBirth;
    
    @Schema(description = "Number of duplicate claims found with matching profile", example = "2")
    private int duplicateCount;
    
    @Schema(description = "List of claims that match the profile")
    private List<Claim> duplicateClaims;
}
