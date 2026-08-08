package com.jobportal.controllers;

import com.jobportal.dto.UserDTO;
import com.jobportal.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Module", description = "Endpoints for platform administration")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "Get all registered users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/verify-company/{userId}")
    @Operation(summary = "Verify an employer's company")
    public ResponseEntity<Void> verifyCompany(@PathVariable Long userId) {
        adminService.verifyCompany(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Suspend/Delete a user")
    public ResponseEntity<Void> suspendUser(@PathVariable Long userId) {
        adminService.suspendUser(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/jobs/{jobId}")
    @Operation(summary = "Delete a job posting")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        adminService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get system-wide analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(adminService.getSystemAnalytics());
    }
}
