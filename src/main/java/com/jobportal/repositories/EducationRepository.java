package com.jobportal.repositories;

import com.jobportal.entities.Education;
import com.jobportal.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByUser(User user);
}
