package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.ProjectMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectMilestoneRepository extends JpaRepository<ProjectMilestone, Long> {
  List<ProjectMilestone> findByGroupId(Long groupId);

  List<ProjectMilestone> findByGroupIdAndStatus(Long groupId, String status);
}
