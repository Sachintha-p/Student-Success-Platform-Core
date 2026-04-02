package com.sliit.studentplatform.module2.repository;

import com.sliit.studentplatform.module2.entity.JobListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Long> {
  // NEW: Fetches active jobs and sorts them so newest ones appear first
  Page<JobListing> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

  Page<JobListing> findByPostedById(Long userId, Pageable pageable);
}