package com.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobCreateRequest {
    private String title;
    private String company;
    private String description;
    private String location;
    private Double salary;
    private String experienceLevel;
    private String jobType;
    private String category;
    private String skillsRequired;
    private LocalDateTime deadline;
}
