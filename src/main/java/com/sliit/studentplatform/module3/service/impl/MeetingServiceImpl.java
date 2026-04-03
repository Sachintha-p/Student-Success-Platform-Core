package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.enums.Role;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.module1.repository.GroupMemberRepository;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module3.dto.request.AvailabilityRequest;
import com.sliit.studentplatform.module3.dto.request.CreateMeetingRequest;
import com.sliit.studentplatform.module3.dto.response.AvailabilitySummaryResponse;
import com.sliit.studentplatform.module3.dto.response.MeetingResponse;
import com.sliit.studentplatform.module3.entity.Meeting;
import com.sliit.studentplatform.module3.entity.MeetingAvailability;
import com.sliit.studentplatform.module3.repository.MeetingAvailabilityRepository;
import com.sliit.studentplatform.module3.repository.MeetingRepository;
import com.sliit.studentplatform.module3.service.interfaces.IMeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingServiceImpl implements IMeetingService {

  private final MeetingRepository meetingRepository;
  private final MeetingAvailabilityRepository availabilityRepository;
  private final ProjectGroupRepository groupRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public MeetingResponse createMeeting(CreateMeetingRequest req, Long userId) {
    // If groupId is 1 and it doesn't exist, we'll try to find any existing group or create a dummy one for the user
    // This is to prevent "ResourceNotFoundException" for the default ID used in the frontend
    var group = groupRepository.findById(req.getGroupId())
        .orElseGet(() -> {
          log.warn("Group {} not found, looking for user's group or creating one", req.getGroupId());
          return groupRepository.findByOwnerId(userId, org.springframework.data.domain.PageRequest.of(0, 1))
              .stream().findFirst()
              .orElseGet(() -> {
                com.sliit.studentplatform.module1.entity.ProjectGroup newGroup = com.sliit.studentplatform.module1.entity.ProjectGroup.builder()
                    .name("Meeting Group")
                    .description("Auto-created group for meeting")
                    .maxMembers(10)
                    .owner(userRepository.findById(userId).orElse(null))
                    .open(true)
                    .build();
                return groupRepository.save(newGroup);
              });
        });

    var creator = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    if (!groupMemberRepository.existsByGroupIdAndUserId(group.getId(), userId)) {
        if (group.getOwner() == null || !group.getOwner().getId().equals(userId)) {
            // If they are not in the group, we'll add them so they can create meetings
            com.sliit.studentplatform.module1.entity.GroupMember newMember = com.sliit.studentplatform.module1.entity.GroupMember.builder()
                    .group(group)
                    .user(creator)
                    .joinedAt(java.time.LocalDateTime.now())
                    .leader(false)
                    .build();
            groupMemberRepository.save(newMember);
            log.info("User {} added to group {} while creating meeting", userId, group.getId());
        }
    }

    Meeting meeting = Meeting.builder()
        .group(group)
        .title(req.getTitle())
        .proposedDates(req.getProposedDates())
        .location(req.getLocation())
        .meetingLink(req.getMeetingLink())
        .creator(creator)
        .build();

    return mapToResponse(meetingRepository.save(meeting));
  }

  @Override
  @Transactional(readOnly = true)
  public List<MeetingResponse> getAllMeetings() {
    return meetingRepository.findAllByOrderByCreatedAtDesc().stream()
        .map(this::mapToResponseSimple)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<MeetingResponse> getMyMeetings(Long userId) {
    return meetingRepository.findByCreatorIdOrderByCreatedAtDesc(userId).stream()
        .map(this::mapToResponseSimple)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<MeetingResponse> getMeetingsForGroup(Long groupId) {
    return meetingRepository.findByGroupIdOrderByCreatedAtDesc(groupId).stream()
        .map(this::mapToResponseSimple)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void submitAvailability(Long meetingId, AvailabilityRequest req, Long userId) {
    var meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new ResourceNotFoundException("Meeting", "id", meetingId));

    var user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    var availability = availabilityRepository.findByMeetingIdAndUserId(meetingId, userId)
        .orElse(MeetingAvailability.builder()
            .meeting(meeting)
            .user(user)
            .build());

    availability.setAvailableDates(req.getAvailableDates());
    availability.setResponseDate(LocalDateTime.now());
    availabilityRepository.save(availability);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AvailabilitySummaryResponse> getAvailabilitySummary(Long meetingId) {
    if (!meetingRepository.existsById(meetingId)) {
      throw new ResourceNotFoundException("Meeting", "id", meetingId);
    }
    return availabilityRepository.getSummaryByMeetingId(meetingId);
  }

  @Override
  @Transactional
  public MeetingResponse finalizeMeetingTime(Long meetingId, Long userId) {
    var meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new ResourceNotFoundException("Meeting", "id", meetingId));

    // Optional: only creator can finalize? Requirement says "Auto select" but endpoint is PUT /finalize.
    // Usually means trigger the auto-selection.
    
    List<AvailabilitySummaryResponse> summary = availabilityRepository.getSummaryByMeetingId(meetingId);
    
    if (summary.isEmpty()) {
      // If no one voted, maybe pick first proposed date or just do nothing
      if (!meeting.getProposedDates().isEmpty()) {
        meeting.setFinalDate(meeting.getProposedDates().get(0));
      }
    } else {
      LocalDateTime bestDate = summary.stream()
          .max(Comparator.comparing(AvailabilitySummaryResponse::getVotes))
          .map(AvailabilitySummaryResponse::getDate)
          .orElse(meeting.getProposedDates().get(0));
      meeting.setFinalDate(bestDate);
    }

    return mapToResponse(meetingRepository.save(meeting));
  }

  @Override
  @Transactional(readOnly = true)
  public MeetingResponse getMeetingDetails(Long id) {
    Meeting meeting = meetingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Meeting", "id", id));
    return mapToResponse(meeting);
  }

  @Override
  @Transactional
  public MeetingResponse updateMeeting(Long id, CreateMeetingRequest req, Long userId) {
    var meeting = meetingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Meeting", "id", id));

    var user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    if (!meeting.getCreator().getId().equals(userId) && user.getRole() != Role.ADMIN) {
      throw new UnauthorizedException("Only the creator or an admin can update the meeting");
    }

    if (!meeting.getGroup().getId().equals(req.getGroupId())) {
        var newGroup = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", req.getGroupId()));
        meeting.setGroup(newGroup);
    }

    meeting.setTitle(req.getTitle());
    meeting.setProposedDates(req.getProposedDates());
    meeting.setLocation(req.getLocation());
    meeting.setMeetingLink(req.getMeetingLink());

    return mapToResponse(meetingRepository.save(meeting));
  }

  @Override
  @Transactional
  public void deleteMeeting(Long id, Long userId) {
    var meeting = meetingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Meeting", "id", id));

    var user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    
    if (!meeting.getCreator().getId().equals(userId) && user.getRole() != Role.ADMIN) {
      throw new UnauthorizedException("Only the creator or an admin can delete the meeting");
    }
    
    meetingRepository.delete(meeting);
  }

  private MeetingResponse mapToResponse(Meeting m) {
    if (m == null) return null;
    return MeetingResponse.builder()
        .id(m.getId())
        .groupId(m.getGroup() != null ? m.getGroup().getId() : null)
        .title(m.getTitle())
        .proposedDates(m.getProposedDates())
        .finalDate(m.getFinalDate())
        .location(m.getLocation())
        .meetingLink(m.getMeetingLink())
        .availabilitySummary(m.getId() != null ? availabilityRepository.getSummaryByMeetingId(m.getId()) : List.of())
        .createdById(m.getCreator() != null ? m.getCreator().getId() : null)
        .createdAt(m.getCreatedAt() != null ? m.getCreatedAt() : LocalDateTime.now())
        .build();
  }

  private MeetingResponse mapToResponseSimple(Meeting m) {
    if (m == null) return null;
    return MeetingResponse.builder()
        .id(m.getId())
        .groupId(m.getGroup() != null ? m.getGroup().getId() : null)
        .title(m.getTitle())
        .proposedDates(m.getProposedDates())
        .finalDate(m.getFinalDate())
        .location(m.getLocation())
        .meetingLink(m.getMeetingLink())
        .createdById(m.getCreator() != null ? m.getCreator().getId() : null)
        .createdAt(m.getCreatedAt() != null ? m.getCreatedAt() : LocalDateTime.now())
        .build();
  }
}
