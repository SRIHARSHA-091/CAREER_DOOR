package com.jobportal.repositories;

import com.jobportal.entities.Application;
import com.jobportal.entities.Job;
import com.jobportal.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUser(User user);
    List<Application> findByJob(Job job);
    Optional<Application> findByUserAndJob(User user, Job job);
    boolean existsByUserAndJob(User user, Job job);
}
