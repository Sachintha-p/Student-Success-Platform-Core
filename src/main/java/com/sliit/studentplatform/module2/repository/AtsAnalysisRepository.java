package com.sliit.studentplatform.module2.repository;

import com.sliit.studentplatform.module2.entity.AtsAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AtsAnalysisRepository extends JpaRepository<AtsAnalysis, Long> {
  List<AtsAnalysis> findByResumeIdOrderByCreatedAtDesc(Long resumeId);
  List<AtsAnalysis> findByResumeId(Long resumeId);
}