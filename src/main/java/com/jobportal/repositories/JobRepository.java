package com.jobportal.repositories;

import com.jobportal.entities.Job;
import com.jobportal.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByIsActiveTrue(Pageable pageable);
    
    List<Job> findByPostedBy(User employer);

    @Query("SELECT j FROM Job j WHERE j.isActive = true AND " +
           "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:skills IS NULL OR j.skillsRequired LIKE CONCAT('%', :skills, '%')) AND " +
           "(:company IS NULL OR LOWER(j.company) LIKE LOWER(CONCAT('%', :company, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minSalary IS NULL OR j.salary >= :minSalary) AND " +
           "(:maxSalary IS NULL OR j.salary <= :maxSalary) AND " +
           "(:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType)")
    Page<Job> searchJobs(@Param("title") String title,
                         @Param("skills") String skills,
                         @Param("company") String company,
                         @Param("location") String location,
                         @Param("minSalary") Double minSalary,
                         @Param("maxSalary") Double maxSalary,
                         @Param("experienceLevel") String experienceLevel,
                         @Param("jobType") String jobType,
                         Pageable pageable);
}
