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
public class SavedJobDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String company;
    private String location;
    private Double salary;
    private LocalDateTime savedAt;
}
