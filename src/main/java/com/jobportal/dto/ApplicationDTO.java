package com.jobportal.dto;

import com.jobportal.entities.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long userId;
    private String username;
    private String resumePath;
    private ApplicationStatus status;
    private String coverLetter;
    private LocalDateTime appliedAt;
}
