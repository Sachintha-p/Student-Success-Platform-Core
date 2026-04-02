package com.sliit.studentplatform.module1.service.impl;

import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.ValidationException;
import com.sliit.studentplatform.module1.dto.response.InvitationResponse;
import com.sliit.studentplatform.module1.entity.GroupInvitation;
import com.sliit.studentplatform.module1.entity.GroupMember;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import com.sliit.studentplatform.module1.entity.enums.InvitationStatus;
import com.sliit.studentplatform.module1.repository.GroupInvitationRepository;
import com.sliit.studentplatform.module1.repository.GroupMemberRepository;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module1.service.interfaces.IInvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationServiceImpl implements IInvitationService {

<<<<<<< feature/Event-managmenr01
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
=======
    private final GroupInvitationRepository invitationRepository;
    private final ProjectGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InvitationResponse> getMyPendingInvitations(Long userId) {
        log.info("Fetching pending invitations for user: {}", userId);
        List<GroupInvitation> invites = invitationRepository.findByInviteeIdAndStatus(userId, InvitationStatus.PENDING);

        return invites.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptInvitation(Long inviteId, Long userId) {
        log.info("User {} accepting invite {}", userId, inviteId);

        GroupInvitation invite = invitationRepository.findByIdAndInviteeId(inviteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", inviteId));

        if (invite.getStatus() != InvitationStatus.PENDING) {
            throw new ValidationException("This invitation has already been processed.");
        }

        ProjectGroup group = invite.getGroup();

        // 1. Check if group is full
        int currentMemberCount = memberRepository.countByGroupId(group.getId());
        if (currentMemberCount >= group.getMaxMembers()) {
            throw new ValidationException("Cannot accept. This team is already full.");
        }

        // 2. Check if already a member
        boolean alreadyMember = memberRepository.findByGroupIdAndUserId(group.getId(), userId).isPresent();
        if (alreadyMember) {
            throw new ValidationException("You are already a member of this team.");
        }

        // 3. Save to GroupMember table
        GroupMember newMember = new GroupMember();
        newMember.setGroup(group);
        newMember.setUser(invite.getInvitee());
        newMember.setLeader(false);
        memberRepository.save(newMember);

        // 4. Add to ProjectGroup internal list
        if (group.getMembers() == null) {
            group.setMembers(new ArrayList<>());
        }
        group.getMembers().add(invite.getInvitee());
        groupRepository.save(group);

        // 5. Mark invite as Accepted
        invite.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invite);
>>>>>>> dev
    }

    @Override
    @Transactional
    public void declineInvitation(Long inviteId, Long userId) {
        log.info("User {} declining invite {}", userId, inviteId);

        GroupInvitation invite = invitationRepository.findByIdAndInviteeId(inviteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", inviteId));

        if (invite.getStatus() != InvitationStatus.PENDING) {
            throw new ValidationException("This invitation has already been processed.");
        }

        invite.setStatus(InvitationStatus.DECLINED);
        invitationRepository.save(invite);
    }

<<<<<<< feature/Event-managmenr01
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
=======
    private InvitationResponse mapToResponse(GroupInvitation invite) {
        return InvitationResponse.builder()
                .id(invite.getId())
                .groupId(invite.getGroup().getId())
                .groupName(invite.getGroup().getName())
                .inviterId(invite.getInviter().getId())
                .inviterName(invite.getInviter().getFullName())
                .status(invite.getStatus().name())
                .createdAt(invite.getCreatedAt())
                .build();
>>>>>>> dev
    }
}