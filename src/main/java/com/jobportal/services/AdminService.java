package com.jobportal.services;

import com.jobportal.dto.UserDTO;
import com.jobportal.entities.Role;
import com.jobportal.entities.User;
import com.jobportal.entities.Job;
import com.jobportal.exceptions.ResourceNotFoundException;
import com.jobportal.repositories.JobRepository;
import com.jobportal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    public void verifyCompany(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setVerified(true);
        userRepository.save(user);
    }

    public void suspendUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void deleteJob(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    public Map<String, Object> getSystemAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalUsers", userRepository.count());
        analytics.put("totalJobs", jobRepository.count());
        analytics.put("totalEmployers", userRepository.findByRole(Role.EMPLOYER).size());
        analytics.put("totalJobSeekers", userRepository.findByRole(Role.JOB_SEEKER).size());
        analytics.put("totalAdmins", userRepository.findByRole(Role.ADMIN).size());
        return analytics;
    }
}
