package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
  List<Project> findByTeamId(Long teamId);
  List<Project> findAllByOrderByCreatedAtDesc();
}
