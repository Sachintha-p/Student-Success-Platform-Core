package com.sliit.studentplatform.module4.repository;

import com.sliit.studentplatform.module4.entity.StudyResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudyResourceRepository extends JpaRepository<StudyResource, Long> {
  List<StudyResource> findBySubjectIgnoreCase(String subject);

  List<StudyResource> findByType(String type);
}
