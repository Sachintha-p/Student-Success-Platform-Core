package com.sliit.studentplatform.module2.repository;

import com.sliit.studentplatform.common.enums.Status;
import com.sliit.studentplatform.module2.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
  List<JobApplication> findByUserId(Long userId);

  List<JobApplication> findByJobListingId(Long jobListingId);

  List<JobApplication> findByJobListingIdAndStatus(Long jobListingId, Status status);

  boolean existsByJobListingIdAndUserId(Long jobListingId, Long userId);
}
