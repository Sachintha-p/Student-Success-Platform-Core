package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module3.dto.request.CreateMilestoneRequest;
import com.sliit.studentplatform.module3.dto.response.MilestoneResponse;
import com.sliit.studentplatform.module3.entity.ProjectMilestone;
import com.sliit.studentplatform.module3.repository.ProjectMilestoneRepository;
import com.sliit.studentplatform.module3.service.interfaces.IMilestoneService;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MilestoneServiceImpl implements IMilestoneService {

  private final ProjectMilestoneRepository milestoneRepository;
  private final ProjectGroupRepository groupRepository;

  @Override
  @Transactional
  public MilestoneResponse createMilestone(CreateMilestoneRequest req, Long userId) {
    var group = groupRepository.findById(req.getGroupId())
        .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", req.getGroupId()));
    var milestone = milestoneRepository.save(ProjectMilestone.builder()
        .group(group).title(req.getTitle()).description(req.getDescription())
        .dueDate(req.getDueDate()).status("PENDING").build());
    return mapToResponse(milestone);
  }

  @Override
  @Transactional(readOnly = true)
  public MilestoneResponse getMilestone(Long id) {
    return mapToResponse(milestoneRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", id)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<MilestoneResponse> getMilestonesForGroup(Long groupId) {
    return milestoneRepository.findByGroupId(groupId).stream()
        .map(this::mapToResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public MilestoneResponse updateMilestoneStatus(Long id, String status, Long userId) {
    var m = milestoneRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", id));
    m.setStatus(status);
    return mapToResponse(milestoneRepository.save(m));
  }

  @Override
  @Transactional
  public void deleteMilestone(Long id, Long userId) {
    milestoneRepository.deleteById(id);
  }

  private MilestoneResponse mapToResponse(ProjectMilestone m) {
    return MilestoneResponse.builder().id(m.getId()).groupId(m.getGroup().getId())
        .title(m.getTitle()).description(m.getDescription())
        .dueDate(m.getDueDate()).status(m.getStatus()).build();
  }
}
