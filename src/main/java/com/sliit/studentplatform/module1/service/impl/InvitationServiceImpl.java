package com.sliit.studentplatform.module1.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.enums.Status;
import com.sliit.studentplatform.common.exception.ConflictException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.module1.dto.request.SendInvitationRequest;
import com.sliit.studentplatform.module1.dto.response.InvitationResponse;
import com.sliit.studentplatform.module1.entity.GroupMember;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import com.sliit.studentplatform.module1.entity.TeamInvitation;
import com.sliit.studentplatform.module1.repository.GroupMemberRepository;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module1.repository.TeamInvitationRepository;
import com.sliit.studentplatform.module1.service.interfaces.IInvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IInvitationService}.
 *
 * <p>
 * Handles ONLY invitation lifecycle (Single Responsibility Principle).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationServiceImpl implements IInvitationService {

  private final TeamInvitationRepository invitationRepository;
  private final ProjectGroupRepository groupRepository;
  private final GroupMemberRepository memberRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public InvitationResponse sendInvitation(SendInvitationRequest request, Long inviterId) {
    log.info("Sending invitation for group {} to user {} from {}",
        request.getGroupId(), request.getInviteeId(), inviterId);

    ProjectGroup group = groupRepository.findById(request.getGroupId())
        .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", request.getGroupId()));

    // Verify inviter is a member of the group and is a leader
    GroupMember inviterMember = memberRepository.findByGroupIdAndUserId(group.getId(), inviterId)
        .orElseThrow(() -> new UnauthorizedException("You are not a member of this group"));
    if (!inviterMember.isLeader()) {
      throw new UnauthorizedException("Only the group leader can send invitations");
    }

    User inviter = inviterMember.getUser();
    User invitee = userRepository.findById(request.getInviteeId())
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getInviteeId()));

    if (memberRepository.existsByGroupIdAndUserId(group.getId(), invitee.getId())) {
      throw new ConflictException("User is already a member of this group");
    }
    if (invitationRepository.existsByGroupIdAndInviteeIdAndStatus(group.getId(), invitee.getId(), Status.PENDING)) {
      throw new ConflictException("A pending invitation already exists for this user");
    }

    TeamInvitation invitation = TeamInvitation.builder()
        .group(group)
        .inviter(inviter)
        .invitee(invitee)
        .message(request.getMessage())
        .status(Status.PENDING)
        .expiresAt(LocalDateTime.now().plusDays(7))
        .build();

    invitation = invitationRepository.save(invitation);
    // TODO: send email/push notification to invitee
    return mapToResponse(invitation);
  }

  @Override
  @Transactional
  public InvitationResponse acceptInvitation(Long invitationId, Long userId) {
    log.info("User {} accepting invitation {}", userId, invitationId);
    TeamInvitation invitation = getInvitationOrThrow(invitationId);
    assertInvitee(invitation, userId);

    invitation.setStatus(Status.ACTIVE);
    invitationRepository.save(invitation);

    // Add user to group
    GroupMember newMember = GroupMember.builder()
        .group(invitation.getGroup())
        .user(invitation.getInvitee())
        .leader(false)
        .build();
    memberRepository.save(newMember);

    return mapToResponse(invitation);
  }

  @Override
  @Transactional
  public InvitationResponse rejectInvitation(Long invitationId, Long userId) {
    log.info("User {} rejecting invitation {}", userId, invitationId);
    TeamInvitation invitation = getInvitationOrThrow(invitationId);
    assertInvitee(invitation, userId);
    invitation.setStatus(Status.REJECTED);
    return mapToResponse(invitationRepository.save(invitation));
  }

  @Override
  @Transactional(readOnly = true)
  public List<InvitationResponse> getPendingInvitationsForUser(Long userId) {
    return invitationRepository.findByInviteeIdAndStatus(userId, Status.PENDING)
        .stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<InvitationResponse> getInvitationsByGroup(Long groupId, Long requesterId) {
    // Verify requester is the group leader
    GroupMember member = memberRepository.findByGroupIdAndUserId(groupId, requesterId)
        .orElseThrow(() -> new UnauthorizedException("You are not a member of this group"));
    if (!member.isLeader()) {
      throw new UnauthorizedException("Only the group leader can view group invitations");
    }

    return invitationRepository.findByGroupIdAndStatus(groupId, Status.PENDING)
        .stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  private TeamInvitation getInvitationOrThrow(Long id) {
    return invitationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("TeamInvitation", "id", id));
  }

  private void assertInvitee(TeamInvitation inv, Long userId) {
    if (!inv.getInvitee().getId().equals(userId)) {
      throw new UnauthorizedException("You are not the invitee of this invitation");
    }
  }

  private InvitationResponse mapToResponse(TeamInvitation inv) {
    return InvitationResponse.builder()
        .id(inv.getId())
        .groupId(inv.getGroup().getId())
        .groupName(inv.getGroup().getName())
        .inviterId(inv.getInviter().getId())
        .inviterName(inv.getInviter().getFullName())
        .inviteeId(inv.getInvitee().getId())
        .inviteeName(inv.getInvitee().getFullName())
        .status(inv.getStatus())
        .message(inv.getMessage())
        .expiresAt(inv.getExpiresAt())
        .createdAt(inv.getCreatedAt())
        .build();
  }
}
