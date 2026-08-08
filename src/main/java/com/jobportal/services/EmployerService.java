package com.jobportal.services;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.entities.ApplicationStatus;
import com.jobportal.entities.Job;
import com.jobportal.entities.User;
import com.jobportal.exceptions.ResourceNotFoundException;
import com.jobportal.repositories.ApplicationRepository;
import com.jobportal.repositories.JobRepository;
import com.jobportal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public Map<String, Object> getDashboardStats(String email) {
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        List<Job> jobs = jobRepository.findByPostedBy(employer);
        
        long totalJobs = jobs.size();
        long activeJobs = jobs.stream().filter(Job::isActive).count();
        long expiredJobs = totalJobs - activeJobs;
        
        long totalApplications = 0;
        long shortlisted = 0;
        long rejected = 0;
        long hired = 0;

        for (Job job : jobs) {
            List<com.jobportal.entities.Application> apps = applicationRepository.findByJob(job);
            totalApplications += apps.size();
            shortlisted += apps.stream().filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED).count();
            rejected += apps.stream().filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count();
            hired += apps.stream().filter(a -> a.getStatus() == ApplicationStatus.SELECTED).count();
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJobs", totalJobs);
        stats.put("activeJobs", activeJobs);
        stats.put("expiredJobs", expiredJobs);
        stats.put("totalApplications", totalApplications);
        stats.put("shortlisted", shortlisted);
        stats.put("rejected", rejected);
        stats.put("hired", hired);

        return stats;
    }
}
