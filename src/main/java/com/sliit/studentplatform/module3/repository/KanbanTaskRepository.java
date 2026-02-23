package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.KanbanTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KanbanTaskRepository extends JpaRepository<KanbanTask, Long> {
  List<KanbanTask> findByGroupId(Long groupId);

  List<KanbanTask> findByGroupIdAndColumn(Long groupId, String column);

  List<KanbanTask> findByAssigneeId(Long assigneeId);
}
