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
    }
}