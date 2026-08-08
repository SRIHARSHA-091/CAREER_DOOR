package com.jobportal.dto;

import com.jobportal.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private String fullName;
    private String phoneNumber;
    private String profilePicturePath;
    
    // Employer fields
    private String companyName;
    private String companyLogoPath;
    private String companyDescription;
    private String companyWebsite;
    private boolean isVerified;
    
    // Job Seeker fields
    private String resumePath;
    private String headline;
    private String summary;
    private String location;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
