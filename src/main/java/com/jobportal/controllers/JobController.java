package com.jobportal.controllers;

import com.jobportal.dto.JobCreateRequest;
import com.jobportal.dto.JobDTO;
import com.jobportal.services.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Management", description = "Endpoints for posting and searching jobs")
public class JobController {

    private final JobService jobService;

    @PostMapping("/post")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Post a new job")
    public ResponseEntity<JobDTO> postJob(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody JobCreateRequest request) {
        return ResponseEntity.ok(jobService.createJob(userDetails.getUsername(), request));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Update an existing job")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @Valid @RequestBody JobCreateRequest request) {
        return ResponseEntity.ok(jobService.updateJob(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @Operation(summary = "Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job details by ID")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping
    @Operation(summary = "Get all active jobs with pagination and sorting")
    public ResponseEntity<Page<JobDTO>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(jobService.getAllJobs(page, size, sortBy, direction));
    }

    @GetMapping("/search")
    @Operation(summary = "Search jobs with filters, pagination and sorting")
    public ResponseEntity<Page<JobDTO>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String jobType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(jobService.searchJobs(title, skills, company, location, minSalary, maxSalary, 
                                                       experienceLevel, jobType, page, size, sortBy, direction));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Close a job posting")
    public ResponseEntity<Void> closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reopen")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Reopen a closed job posting")
    public ResponseEntity<Void> reopenJob(@PathVariable Long id) {
        jobService.reopenJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get all jobs posted by the current employer")
    public ResponseEntity<List<JobDTO>> getMyJobs(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.getJobsByEmployer(userDetails.getUsername()));
    }
}
