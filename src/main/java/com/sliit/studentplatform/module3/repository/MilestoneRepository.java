package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.Milestone;
import com.sliit.studentplatform.module3.enums.MilestoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
  List<Milestone> findByProjectIdOrderByStartDateAsc(Long projectId);
  List<Milestone> findByProjectIdAndStatusNotAndDueDateBefore(Long projectId, MilestoneStatus status, LocalDate date);
  List<Milestone> findByProjectIdAndStatusNotAndDueDateBetween(Long projectId, MilestoneStatus status, LocalDate start, LocalDate end);
  List<Milestone> findByStatusNotAndDueDateBetween(MilestoneStatus status, LocalDate start, LocalDate end);
  List<Milestone> findByStatusNotAndDueDateBefore(MilestoneStatus status, LocalDate date);
}
