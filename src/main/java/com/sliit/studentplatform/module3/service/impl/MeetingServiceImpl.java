package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module3.dto.request.CreateMeetingRequest;
import com.sliit.studentplatform.module3.dto.response.MeetingResponse;
import com.sliit.studentplatform.module3.entity.Meeting;
import com.sliit.studentplatform.module3.repository.MeetingRepository;
import com.sliit.studentplatform.module3.service.interfaces.IMeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingServiceImpl implements IMeetingService {

  private final MeetingRepository meetingRepository;
  private final ProjectGroupRepository groupRepository;

  @Override
  @Transactional
  public MeetingResponse createMeeting(CreateMeetingRequest req, Long userId) {
    var group = groupRepository.findById(req.getGroupId())
        .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", req.getGroupId()));
    return mapToResponse(meetingRepository.save(Meeting.builder()
        .group(group).title(req.getTitle()).agenda(req.getAgenda())
        .meetingTime(req.getMeetingTime()).durationMinutes(req.getDurationMinutes())
        .meetingLink(req.getMeetingLink()).status("SCHEDULED").build()));
  }

  @Override
  @Transactional(readOnly = true)
  public MeetingResponse getMeeting(Long id) {
    return mapToResponse(meetingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Meeting", "id", id)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<MeetingResponse> getMeetingsForGroup(Long groupId) {
    return meetingRepository.findByGroupId(groupId).stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public MeetingResponse updateMeetingStatus(Long id, String status, Long userId) {
    var m = meetingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Meeting", "id", id));
    m.setStatus(status);
    return mapToResponse(meetingRepository.save(m));
  }

  private MeetingResponse mapToResponse(Meeting m) {
    return MeetingResponse.builder().id(m.getId()).groupId(m.getGroup().getId())
        .title(m.getTitle()).agenda(m.getAgenda()).meetingTime(m.getMeetingTime())
        .durationMinutes(m.getDurationMinutes()).meetingLink(m.getMeetingLink())
        .status(m.getStatus()).build();
  }
}
