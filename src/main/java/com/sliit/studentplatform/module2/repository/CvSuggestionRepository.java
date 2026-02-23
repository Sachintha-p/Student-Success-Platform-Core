package com.sliit.studentplatform.module2.repository;

import com.sliit.studentplatform.module2.entity.CvSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvSuggestionRepository extends JpaRepository<CvSuggestion, Long> {
  List<CvSuggestion> findByResumeIdAndAppliedFalse(Long resumeId);
}
