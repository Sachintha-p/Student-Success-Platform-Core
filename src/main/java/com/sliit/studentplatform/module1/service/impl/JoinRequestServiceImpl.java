package com.sliit.studentplatform.module1.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.enums.Status;
import com.sliit.studentplatform.common.exception.ConflictException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.module1.dto.request.JoinRequestDto;
import com.sliit.studentplatform.module1.dto.response.InvitationResponse;
import com.sliit.studentplatform.module1.entity.GroupMember;
import com.sliit.studentplatform.module1.entity.JoinRequest;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import com.sliit.studentplatform.module1.repository.GroupMemberRepository;
import com.sliit.studentplatform.module1.repository.JoinRequestRepository;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module1.service.interfaces.IJoinRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IJoinRequestService}.
 *
 * <p>
 * Handles ONLY join request lifecycle (Single Responsibility Principle).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JoinRequestServiceImpl implements IJoinRequestService {

    private final JoinRequestRepository joinRequestRepository;
    private final ProjectGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public InvitationResponse submitJoinRequest(JoinRequestDto request, Long requesterId) {
        log.info("User {} submitting join request to group {}", requesterId, request.getGroupId());

        ProjectGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", request.getGroupId()));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requesterId));

        if (memberRepository.existsByGroupIdAndUserId(group.getId(), requesterId)) {
            throw new ConflictException("You are already a member of this group");
        }
        if (joinRequestRepository.existsByGroupIdAndRequesterIdAndStatus(
                group.getId(), requesterId, Status.PENDING)) {
            throw new ConflictException("You already have a pending join request for this group");
        }

        JoinRequest joinRequest = JoinRequest.builder()
                .group(group)
                .requester(requester)
                .message(request.getMessage())
                .status(Status.PENDING)
                .build();

        joinRequest = joinRequestRepository.save(joinRequest);
        // TODO: notify group owner of new join request
        return mapToResponse(joinRequest);
    }

    @Override
    @Transactional
    public InvitationResponse approveJoinRequest(Long requestId, Long approverId) {
        log.info("Approving join request {} by user {}", requestId, approverId);
        JoinRequest joinRequest = getOrThrow(requestId);
        // TODO: assert approverId is the group owner
        joinRequest.setStatus(Status.ACTIVE);
        joinRequestRepository.save(joinRequest);

        GroupMember newMember = GroupMember.builder()
                .group(joinRequest.getGroup())
                .user(joinRequest.getRequester())
                .leader(false)
                .build();
        memberRepository.save(newMember);

        return mapToResponse(joinRequest);
    }

    @Override
    @Transactional
    public InvitationResponse rejectJoinRequest(Long requestId, Long approverId) {
        log.info("Rejecting join request {} by user {}", requestId, approverId);
        JoinRequest joinRequest = getOrThrow(requestId);
        joinRequest.setStatus(Status.REJECTED);
        return mapToResponse(joinRequestRepository.save(joinRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvitationResponse> getPendingRequestsForGroup(Long groupId, Long currentUserId) {
        // TODO: assert currentUserId is group owner
        return joinRequestRepository.findByGroupIdAndStatus(groupId, Status.PENDING)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvitationResponse> getMyJoinRequests(Long userId) {
        return joinRequestRepository.findByRequesterId(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private JoinRequest getOrThrow(Long id) {
        return joinRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JoinRequest", "id", id));
    }

    private InvitationResponse mapToResponse(JoinRequest jr) {
        return InvitationResponse.builder()
                .id(jr.getId())
                .groupId(jr.getGroup().getId())
                .groupName(jr.getGroup().getName())
                .inviterId(jr.getRequester().getId())
                // 🔧 FIX: Changed inviteeName to inviterName to match the DTO!
                .inviterName(jr.getRequester().getFullName())
                // 🔧 PRO-TIP: Added .name() so the Enum maps cleanly to the String status field in your DTO
                // (If you changed the DTO field to use the Status enum, just remove .name())
                .status(jr.getStatus().name())
                .message(jr.getMessage())
                .createdAt(jr.getCreatedAt())
                .build();
    }
}