package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.Task;
import com.sliit.studentplatform.module3.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectIdOrderByPositionAsc(Long projectId);
    List<Task> findByMilestoneIdOrderByPositionAsc(Long milestoneId);
    List<Task> findByAssignedToIdOrderByPositionAsc(Long userId);
    List<Task> findByStatusOrderByPositionAsc(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.project.team.id = :groupId ORDER BY t.position ASC")
    List<Task> findByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.project.team.id = :groupId GROUP BY t.status")
    List<Object[]> countTasksByStatusForGroup(@Param("groupId") Long groupId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.team.id = :groupId")
    long countTotalTasksForGroup(@Param("groupId") Long groupId);
}
