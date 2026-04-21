package com.sliit.studentplatform.module2.repository;

import com.sliit.studentplatform.common.enums.Status;
import com.sliit.studentplatform.module2.entity.JobApplication;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

  // Forces Spring to fetch the related entities immediately in one single SQL query!
  @Override
  @EntityGraph(attributePaths = {"jobListing", "user", "resume"})
  List<JobApplication> findAll();

  @EntityGraph(attributePaths = {"jobListing", "user", "resume"})
  List<JobApplication> findByUserId(Long userId);

  @EntityGraph(attributePaths = {"jobListing", "user", "resume"})
  List<JobApplication> findByJobListingId(Long jobListingId);

  List<JobApplication> findByJobListingIdAndStatus(Long jobListingId, Status status);

  boolean existsByJobListingIdAndUserId(Long jobListingId, Long userId);
}