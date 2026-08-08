package com.jobportal.controllers;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.dto.ApplicationRequest;
import com.jobportal.entities.ApplicationStatus;
import com.jobportal.services.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Job Applications", description = "Endpoints for applying to jobs and managing applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Apply for a job")
    public ResponseEntity<ApplicationDTO> applyToJob(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.applyToJob(userDetails.getUsername(), request));
    }

    @PatchMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Withdraw a job application")
    public ResponseEntity<Void> withdrawApplication(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        applicationService.withdrawApplication(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Get all applications submitted by the current user")
    public ResponseEntity<List<ApplicationDTO>> getMyApplications(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(applicationService.getUserApplications(userDetails.getUsername()));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get all applications for a specific job (Employer only)")
    public ResponseEntity<List<ApplicationDTO>> getJobApplications(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getJobApplications(jobId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @Operation(summary = "Update the status of a job application")
    public ResponseEntity<Void> updateApplicationStatus(@PathVariable Long id, @RequestParam ApplicationStatus status) {
        applicationService.updateApplicationStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
