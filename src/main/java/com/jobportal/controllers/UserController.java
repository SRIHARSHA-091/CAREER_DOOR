package com.jobportal.controllers;

import com.jobportal.dto.EducationDTO;
import com.jobportal.dto.ExperienceDTO;
import com.jobportal.dto.SkillDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Endpoints for managing user profiles")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserDTO> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getUsername(), userDTO));
    }

    @PostMapping("/profile/picture")
    @Operation(summary = "Upload profile picture")
    public ResponseEntity<String> uploadProfilePicture(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadProfilePicture(userDetails.getUsername(), file));
    }

    @PostMapping("/profile/resume")
    @Operation(summary = "Upload resume")
    public ResponseEntity<String> uploadResume(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadResume(userDetails.getUsername(), file));
    }

    @GetMapping("/profile/completion")
    @Operation(summary = "Get profile completion percentage")
    public ResponseEntity<Double> getProfileCompletion(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfileCompletionPercentage(userDetails.getUsername()));
    }

    // Education
    @PostMapping("/profile/education")
    @Operation(summary = "Add education to profile")
    public ResponseEntity<EducationDTO> addEducation(@AuthenticationPrincipal UserDetails userDetails, @RequestBody EducationDTO educationDTO) {
        return ResponseEntity.ok(userService.addEducation(userDetails.getUsername(), educationDTO));
    }

    @GetMapping("/profile/education")
    @Operation(summary = "Get all education entries")
    public ResponseEntity<List<EducationDTO>> getEducation(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getEducation(userDetails.getUsername()));
    }

    @DeleteMapping("/profile/education/{id}")
    @Operation(summary = "Delete education from profile")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long id) {
        userService.deleteEducation(id);
        return ResponseEntity.noContent().build();
    }

    // Experience
    @PostMapping("/profile/experience")
    @Operation(summary = "Add experience to profile")
    public ResponseEntity<ExperienceDTO> addExperience(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ExperienceDTO experienceDTO) {
        return ResponseEntity.ok(userService.addExperience(userDetails.getUsername(), experienceDTO));
    }

    @GetMapping("/profile/experience")
    @Operation(summary = "Get all experience entries")
    public ResponseEntity<List<ExperienceDTO>> getExperience(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getExperience(userDetails.getUsername()));
    }

    @DeleteMapping("/profile/experience/{id}")
    @Operation(summary = "Delete experience from profile")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        userService.deleteExperience(id);
        return ResponseEntity.noContent().build();
    }

    // Skills
    @PostMapping("/profile/skills")
    @Operation(summary = "Add skill to profile")
    public ResponseEntity<SkillDTO> addSkill(@AuthenticationPrincipal UserDetails userDetails, @RequestBody SkillDTO skillDTO) {
        return ResponseEntity.ok(userService.addSkill(userDetails.getUsername(), skillDTO));
    }

    @GetMapping("/profile/skills")
    @Operation(summary = "Get all skills")
    public ResponseEntity<List<SkillDTO>> getSkills(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getSkills(userDetails.getUsername()));
    }

    @DeleteMapping("/profile/skills/{id}")
    @Operation(summary = "Delete skill from profile")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        userService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
