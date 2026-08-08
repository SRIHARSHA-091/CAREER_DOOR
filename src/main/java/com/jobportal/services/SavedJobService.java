package com.jobportal.services;

import com.jobportal.dto.SavedJobDTO;
import com.jobportal.entities.SavedJob;
import com.jobportal.entities.User;
import com.jobportal.entities.Job;
import com.jobportal.exceptions.ResourceNotFoundException;
import com.jobportal.repositories.SavedJobRepository;
import com.jobportal.repositories.JobRepository;
import com.jobportal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobService {
    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public void saveJob(String email, Long jobId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        
        if (!savedJobRepository.existsByUserAndJob(user, job)) {
            SavedJob savedJob = SavedJob.builder().user(user).job(job).build();
            savedJobRepository.save(savedJob);
        }
    }

    public void unsaveJob(String email, Long jobId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        savedJobRepository.findByUserAndJob(user, job).ifPresent(savedJobRepository::delete);
    }

    public List<SavedJobDTO> getSavedJobs(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return savedJobRepository.findByUser(user).stream()
                .map(sj -> {
                    SavedJobDTO dto = SavedJobDTO.builder()
                            .id(sj.getId())
                            .jobId(sj.getJob().getId())
                            .jobTitle(sj.getJob().getTitle())
                            .company(sj.getJob().getCompany())
                            .location(sj.getJob().getLocation())
                            .salary(sj.getJob().getSalary())
                            .savedAt(sj.getSavedAt())
                            .build();
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
