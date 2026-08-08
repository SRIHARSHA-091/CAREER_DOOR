package com.jobportal.services;

import com.jobportal.dto.EducationDTO;
import com.jobportal.dto.ExperienceDTO;
import com.jobportal.dto.SkillDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.entities.Education;
import com.jobportal.entities.Experience;
import com.jobportal.entities.Role;
import com.jobportal.entities.Skill;
import com.jobportal.entities.User;
import com.jobportal.exceptions.ResourceNotFoundException;
import com.jobportal.repositories.EducationRepository;
import com.jobportal.repositories.ExperienceRepository;
import com.jobportal.repositories.SkillRepository;
import com.jobportal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;

    public UserDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO updateProfile(String email, UserDTO userDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update fields
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setHeadline(userDTO.getHeadline());
        user.setSummary(userDTO.getSummary());
        user.setLocation(userDTO.getLocation());
        
        // Employer fields
        if (user.getRole() == Role.EMPLOYER) {
            user.setCompanyName(userDTO.getCompanyName());
            user.setCompanyDescription(userDTO.getCompanyDescription());
            user.setCompanyWebsite(userDTO.getCompanyWebsite());
        }

        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    public String uploadProfilePicture(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String filePath = fileStorageService.storeFile(file, "profiles");
        user.setProfilePicturePath(filePath);
        userRepository.save(user);
        return filePath;
    }

    public String uploadResume(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String filePath = fileStorageService.storeFile(file, "resumes");
        user.setResumePath(filePath);
        userRepository.save(user);
        return filePath;
    }
    
    public double getProfileCompletionPercentage(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        int totalFields = 6;
        int completedFields = 0;
        
        if (user.getFullName() != null && !user.getFullName().isEmpty()) completedFields++;
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) completedFields++;
        if (user.getHeadline() != null && !user.getHeadline().isEmpty()) completedFields++;
        if (user.getSummary() != null && !user.getSummary().isEmpty()) completedFields++;
        if (user.getLocation() != null && !user.getLocation().isEmpty()) completedFields++;
        if (user.getResumePath() != null && !user.getResumePath().isEmpty()) completedFields++;
        
        return (double) completedFields / totalFields * 100;
    }

    // Education
    public EducationDTO addEducation(String email, EducationDTO educationDTO) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Education education = modelMapper.map(educationDTO, Education.class);
        education.setUser(user);
        Education saved = educationRepository.save(education);
        return modelMapper.map(saved, EducationDTO.class);
    }

    public List<EducationDTO> getEducation(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return educationRepository.findByUser(user).stream()
                .map(e -> modelMapper.map(e, EducationDTO.class))
                .collect(Collectors.toList());
    }
    
    public void deleteEducation(Long id) {
        educationRepository.deleteById(id);
    }

    // Experience
    public ExperienceDTO addExperience(String email, ExperienceDTO experienceDTO) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Experience experience = modelMapper.map(experienceDTO, Experience.class);
        experience.setUser(user);
        Experience saved = experienceRepository.save(experience);
        return modelMapper.map(saved, ExperienceDTO.class);
    }

    public List<ExperienceDTO> getExperience(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return experienceRepository.findByUser(user).stream()
                .map(e -> modelMapper.map(e, ExperienceDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteExperience(Long id) {
        experienceRepository.deleteById(id);
    }

    // Skills
    public SkillDTO addSkill(String email, SkillDTO skillDTO) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Skill skill = modelMapper.map(skillDTO, Skill.class);
        skill.setUser(user);
        Skill saved = skillRepository.save(skill);
        return modelMapper.map(saved, SkillDTO.class);
    }

    public List<SkillDTO> getSkills(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return skillRepository.findByUser(user).stream()
                .map(s -> modelMapper.map(s, SkillDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }
}
