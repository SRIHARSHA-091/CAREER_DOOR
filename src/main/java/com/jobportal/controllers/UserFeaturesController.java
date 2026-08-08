package com.jobportal.controllers;

import com.jobportal.dto.JobDTO;
import com.jobportal.dto.NotificationDTO;
import com.jobportal.dto.SavedJobDTO;
import com.jobportal.services.NotificationService;
import com.jobportal.services.SavedJobService;
import com.jobportal.services.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "User Features", description = "Endpoints for notifications, saved jobs, and recommendations")
public class UserFeaturesController {

    private final NotificationService notificationService;
    private final SavedJobService savedJobService;
    private final RecommendationService recommendationService;

    @GetMapping("/notifications")
    @Operation(summary = "Get current user's notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userDetails.getUsername()));
    }

    @PatchMapping("/notifications/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/jobs/{jobId}/save")
    @Operation(summary = "Save a job for later")
    public ResponseEntity<Void> saveJob(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long jobId) {
        savedJobService.saveJob(userDetails.getUsername(), jobId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/jobs/{jobId}/unsave")
    @Operation(summary = "Remove a saved job")
    public ResponseEntity<Void> unsaveJob(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long jobId) {
        savedJobService.unsaveJob(userDetails.getUsername(), jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/saved")
    @Operation(summary = "Get all saved jobs for current user")
    public ResponseEntity<List<SavedJobDTO>> getSavedJobs(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(savedJobService.getSavedJobs(userDetails.getUsername()));
    }

    @GetMapping("/jobs/recommendations")
    @Operation(summary = "Get job recommendations based on profile")
    public ResponseEntity<List<JobDTO>> getRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(recommendationService.recommendJobs(userDetails.getUsername()));
    }
}
