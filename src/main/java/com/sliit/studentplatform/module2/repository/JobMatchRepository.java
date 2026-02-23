package com.sliit.studentplatform.module2.repository;

import com.sliit.studentplatform.module2.entity.JobMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobMatchRepository extends JpaRepository<JobMatch, Long> {
  List<JobMatch> findByUserIdOrderByMatchScoreDesc(Long userId);

  List<JobMatch> findByJobListingId(Long jobListingId);
}
