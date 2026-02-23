package com.sliit.studentplatform.module2.repository;

import com.sliit.studentplatform.module2.entity.JobListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Long> {
  Page<JobListing> findByActiveTrue(Pageable pageable);

  Page<JobListing> findByPostedById(Long userId, Pageable pageable);
}
