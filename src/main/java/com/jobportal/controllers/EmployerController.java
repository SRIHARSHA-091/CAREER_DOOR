package com.jobportal.controllers;

import com.jobportal.services.EmployerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/employer")
@RequiredArgsConstructor
@Tag(name = "Employer Module", description = "Endpoints for employer dashboard and management")
public class EmployerController {

    private final EmployerService employerService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get employer dashboard statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(employerService.getDashboardStats(userDetails.getUsername()));
    }
}
