package com.jobportal.services;

import com.jobportal.dto.JobDTO;
import com.jobportal.entities.Job;
import com.jobportal.entities.User;
import com.jobportal.exceptions.ResourceNotFoundException;
import com.jobportal.repositories.JobRepository;
import com.jobportal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<JobDTO> recommendJobs(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Basic recommendation logic: match by location or skills (if implemented)
        // For now, let's just recommend active jobs in the same location
        String location = user.getLocation();
        
        List<Job> recommendedJobs;
        if (location != null && !location.isEmpty()) {
            recommendedJobs = jobRepository.findAll().stream()
                    .filter(Job::isActive)
                    .filter(j -> j.getLocation().equalsIgnoreCase(location))
                    .limit(5)
                    .collect(Collectors.toList());
        } else {
            recommendedJobs = jobRepository.findAll().stream()
                    .filter(Job::isActive)
                    .limit(5)
                    .collect(Collectors.toList());
        }

        return recommendedJobs.stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .collect(Collectors.toList());
    }
}
