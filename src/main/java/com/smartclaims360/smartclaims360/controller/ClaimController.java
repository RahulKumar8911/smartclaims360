package com.smartclaims360.smartclaims360.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClaimController {

    @GetMapping("/health")
    public String health() {
        return "SmartClaims360 API is running";
    }

}
