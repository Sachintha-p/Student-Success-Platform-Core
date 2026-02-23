package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.MilestoneTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MilestoneTaskRepository extends JpaRepository<MilestoneTask, Long> {
  List<MilestoneTask> findByMilestoneId(Long milestoneId);

  List<MilestoneTask> findByAssigneeId(Long assigneeId);
}
