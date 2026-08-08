package com.jobportal.services;

import com.jobportal.dto.JobCreateRequest;
import com.jobportal.dto.JobDTO;
import com.jobportal.entities.Job;
import com.jobportal.entities.User;
import com.jobportal.exceptions.ResourceNotFoundException;
import com.jobportal.repositories.JobRepository;
import com.jobportal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public JobDTO createJob(String employerEmail, JobCreateRequest request) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        Job job = modelMapper.map(request, Job.class);
        job.setPostedBy(employer);
        job.setActive(true);

        Job savedJob = jobRepository.save(job);
        return modelMapper.map(savedJob, JobDTO.class);
    }

    public JobDTO updateJob(Long jobId, JobCreateRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        modelMapper.map(request, job);
        Job updatedJob = jobRepository.save(job);
        return modelMapper.map(updatedJob, JobDTO.class);
    }

    public void deleteJob(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    public JobDTO getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return modelMapper.map(job, JobDTO.class);
    }

    public Page<JobDTO> getAllJobs(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return jobRepository.findByIsActiveTrue(pageable).map(job -> modelMapper.map(job, JobDTO.class));
    }

    public Page<JobDTO> searchJobs(String title, String skills, String company, String location,
                                   Double minSalary, Double maxSalary, String experienceLevel,
                                   String jobType, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return jobRepository.searchJobs(title, skills, company, location, minSalary, maxSalary, 
                                        experienceLevel, jobType, pageable)
                .map(job -> modelMapper.map(job, JobDTO.class));
    }

    public void closeJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setActive(false);
        jobRepository.save(job);
    }

    public void reopenJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setActive(true);
        jobRepository.save(job);
    }

    public List<JobDTO> getJobsByEmployer(String email) {
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        return jobRepository.findByPostedBy(employer).stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .collect(Collectors.toList());
    }
}
