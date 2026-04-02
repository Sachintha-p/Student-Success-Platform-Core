package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.module3.dto.request.ProjectRequest;
import com.sliit.studentplatform.module3.dto.response.ProjectResponse;

import java.util.List;

public interface IProjectService {
  ProjectResponse createProject(ProjectRequest req, Long userId);
  ProjectResponse getProjectById(Long id);
  List<ProjectResponse> getAllProjects();
  List<ProjectResponse> getProjectsByTeam(Long teamId);
  ProjectResponse updateProject(Long id, ProjectRequest req, Long userId);
  void deleteProject(Long id, Long userId);
  double calculateProjectProgress(Long id);
}
