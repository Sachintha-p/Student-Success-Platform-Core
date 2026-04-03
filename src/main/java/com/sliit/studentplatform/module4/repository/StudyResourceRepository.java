package com.sliit.studentplatform.module4.repository;

import com.sliit.studentplatform.module4.entity.StudyResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudyResourceRepository extends JpaRepository<StudyResource, Long> {
  List<StudyResource> findBySubjectIgnoreCase(String subject);

  List<StudyResource> findBySubjectContainingIgnoreCase(String subject);

  List<StudyResource> findByType(String type);

  List<StudyResource> findByTitleContainingIgnoreCase(String title);

  @Query(value = "SELECT * FROM study_resources WHERE :tag = ANY(tags)", nativeQuery = true)
  List<StudyResource> findByTag(String tag);

  @Query("SELECT s.subject, COUNT(s) FROM StudyResource s GROUP BY s.subject ORDER BY COUNT(s) DESC")
  List<Object[]> countResourcesBySubject();

  @Query("SELECT s.type, COUNT(s) FROM StudyResource s GROUP BY s.type")
  List<Object[]> countResourcesByType();
}
