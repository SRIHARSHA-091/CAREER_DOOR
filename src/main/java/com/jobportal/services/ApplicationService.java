package com.jobportal.services;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.dto.ApplicationRequest;
import com.jobportal.entities.Application;
import com.jobportal.entities.ApplicationStatus;
import com.jobportal.entities.Job;
import com.jobportal.entities.User;
import com.jobportal.exceptions.BadRequestException;
import com.jobportal.exceptions.ResourceNotFoundException;
import com.jobportal.repositories.ApplicationRepository;
import com.jobportal.repositories.JobRepository;
import com.jobportal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public ApplicationDTO applyToJob(String userEmail, ApplicationRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.isActive()) {
            throw new BadRequestException("This job is no longer active");
        }

        if (applicationRepository.existsByUserAndJob(user, job)) {
            throw new BadRequestException("You have already applied for this job");
        }

        Application application = Application.builder()
                .user(user)
                .job(job)
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.APPLIED)
                .resumePath(user.getResumePath()) // Use the resume from profile
                .build();

        Application savedApplication = applicationRepository.save(application);
        
        // Notify user
        notificationService.createNotification(user, "You have successfully applied for " + job.getTitle(), "APPLICATION_SUBMITTED");
        emailService.sendEmail(user.getEmail(), "Application Submitted", "You have applied for the job: " + job.getTitle());
        
        return mapToDTO(savedApplication);
    }

    public void withdrawApplication(String userEmail, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You can only withdraw your own applications");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(application);
    }

    public List<ApplicationDTO> getUserApplications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return applicationRepository.findByUser(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ApplicationDTO> getJobApplications(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return applicationRepository.findByJob(job).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        application.setStatus(status);
        applicationRepository.save(application);
        
        // Notify user
        String message = "Your application for " + application.getJob().getTitle() + " status updated to: " + status;
        notificationService.createNotification(application.getUser(), message, "APPLICATION_STATUS_UPDATE");
        emailService.sendEmail(application.getUser().getEmail(), "Application Status Update", message);
    }

    private ApplicationDTO mapToDTO(Application application) {
        ApplicationDTO dto = modelMapper.map(application, ApplicationDTO.class);
        dto.setJobId(application.getJob().getId());
        dto.setJobTitle(application.getJob().getTitle());
        dto.setCompanyName(application.getJob().getCompany());
        dto.setUserId(application.getUser().getId());
        dto.setUsername(application.getUser().getUsername());
        return dto;
    }
}
