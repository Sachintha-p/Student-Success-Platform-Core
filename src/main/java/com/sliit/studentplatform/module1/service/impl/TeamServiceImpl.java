package com.sliit.studentplatform.module1.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.common.exception.ValidationException;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module1.dto.request.CreateGroupRequest;
import com.sliit.studentplatform.module1.dto.response.GroupResponse;
import com.sliit.studentplatform.module1.dto.response.JoinRequestResponse;
import com.sliit.studentplatform.module1.entity.GroupMember;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import com.sliit.studentplatform.module1.entity.GroupJoinRequest;
import com.sliit.studentplatform.module1.repository.GroupJoinRequestRepository;
import com.sliit.studentplatform.module1.repository.GroupMemberRepository;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module1.service.interfaces.ITeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sliit.studentplatform.module1.entity.GroupInvitation;
import com.sliit.studentplatform.module1.repository.GroupInvitationRepository;
import com.sliit.studentplatform.module1.entity.enums.InvitationStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements ITeamService {

    private final ProjectGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final GroupJoinRequestRepository joinRequestRepository;

    private static final int MAX_GROUPS_PER_USER = 5;

    @Override
    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId).orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId));
        ProjectGroup group = new ProjectGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());
        if (request.getRequiredSkills() != null) group.setRequiredSkills(new ArrayList<>(Arrays.asList(request.getRequiredSkills())));
        group.setSubject(request.getSubject());
        group.setYearOfStudy(request.getYearOfStudy());
        group.setSemester(request.getSemester());
        group.setOwner(creator);
        group.setOpen(true);
        if (group.getMembers() == null) group.setMembers(new ArrayList<>());
        group.getMembers().add(creator);
        group = groupRepository.save(group);

        GroupMember leaderMembership = new GroupMember();
        leaderMembership.setGroup(group);
        leaderMembership.setUser(creator);
        leaderMembership.setLeader(true);
        memberRepository.save(leaderMembership);
        return mapToResponse(group, 1);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long groupId) {
        ProjectGroup group = findGroupOrThrow(groupId);
        return mapToResponse(group, memberRepository.countByGroupId(groupId));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<GroupResponse> listOpenGroups(Pageable pageable) {
        Page<ProjectGroup> page = groupRepository.findByOpenTrue(pageable);
        return PagedResponse.of(page.map(g -> mapToResponse(g, memberRepository.countByGroupId(g.getId()))));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<GroupResponse> searchGroups(String keyword, Pageable pageable) {
        Page<ProjectGroup> page = groupRepository.searchByKeyword(keyword, pageable);
        return PagedResponse.of(page.map(g -> mapToResponse(g, memberRepository.countByGroupId(g.getId()))));
    }

    @Override
    @Transactional
    public GroupResponse updateGroup(Long groupId, CreateGroupRequest request, Long currentUserId) {
        ProjectGroup group = findGroupOrThrow(groupId);
        assertOwner(group, currentUserId);
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());
        if (request.getRequiredSkills() != null) {
            if (group.getRequiredSkills() == null) group.setRequiredSkills(new ArrayList<>());
            group.getRequiredSkills().clear();
            group.getRequiredSkills().addAll(Arrays.asList(request.getRequiredSkills()));
        }
        group.setSubject(request.getSubject());
        group.setYearOfStudy(request.getYearOfStudy());
        group.setSemester(request.getSemester());
        group = groupRepository.save(group);
        return mapToResponse(group, memberRepository.countByGroupId(groupId));
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId, Long currentUserId) {
        ProjectGroup group = findGroupOrThrow(groupId);
        assertOwner(group, currentUserId);
        groupRepository.delete(group);
    }

    @Override
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        GroupMember membership = memberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        if (membership.isLeader()) throw new ValidationException("Group leader cannot leave the group.");
        memberRepository.delete(membership);
        ProjectGroup group = findGroupOrThrow(groupId);
        if (group.getMembers() != null) {
            group.getMembers().removeIf(user -> user.getId().equals(userId));
            groupRepository.save(group);
        }
    }

    @Override
    @Transactional
    public void joinGroup(Long groupId, Long userId, String message) {
        ProjectGroup group = findGroupOrThrow(groupId);
        User user = userRepository.findById(userId).orElseThrow();

        if (!group.isOpen()) throw new IllegalStateException("This group is closed.");

        if (memberRepository.findByGroupIdAndUserId(groupId, userId).isPresent()) {
            throw new IllegalStateException("You are already a member of this group.");
        }

        if (joinRequestRepository.existsByGroupIdAndStudentIdAndStatus(groupId, userId, "PENDING")) {
            throw new IllegalStateException("You already have a pending join request for this team.");
        }

        GroupJoinRequest request = new GroupJoinRequest();
        request.setGroup(group);
        request.setStudent(user);
        request.setStatus("PENDING");
        request.setMessage(message);
        joinRequestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<GroupResponse> getMyGroups(Long userId, Pageable pageable) {
        Page<ProjectGroup> page = groupRepository.findByMembers_Id(userId, pageable);
        return PagedResponse.of(page.map(g -> mapToResponse(g, memberRepository.countByGroupId(g.getId()))));
    }

    // =================================================================================
    // ================== NEW: JOIN REQUEST MANAGEMENT FOR OWNERS ======================
    // =================================================================================

    @Override
    @Transactional(readOnly = true)
    public List<JoinRequestResponse> getPendingJoinRequests(Long ownerId) {
        List<GroupJoinRequest> requests = joinRequestRepository.findByGroupOwnerIdAndStatus(ownerId, "PENDING");
        return requests.stream().map(req -> JoinRequestResponse.builder()
                .id(req.getId())
                .groupId(req.getGroup().getId())
                .groupName(req.getGroup().getName())
                .inviterId(req.getStudent().getId())
                .inviterName(req.getStudent().getFullName())
                .message(req.getMessage())
                .status(req.getStatus())
                .createdAt(req.getCreatedAt())
                .studentSkills(new ArrayList<>())
                .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptJoinRequest(Long requestId, Long ownerId) {
        GroupJoinRequest request = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Join Request", "id", requestId));

        if (!request.getGroup().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("Only team owners can accept requests.");
        }

        ProjectGroup group = request.getGroup();
        int currentMembers = memberRepository.countByGroupId(group.getId());
        if (currentMembers >= group.getMaxMembers()) {
            throw new ValidationException("Cannot accept. Team is already full.");
        }

        GroupMember newMember = new GroupMember();
        newMember.setGroup(group);
        newMember.setUser(request.getStudent());
        newMember.setLeader(false);
        memberRepository.save(newMember);

        request.setStatus("ACCEPTED");
        joinRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void declineJoinRequest(Long requestId, Long ownerId) {
        GroupJoinRequest request = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Join Request", "id", requestId));

        if (!request.getGroup().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("Only team owners can decline requests.");
        }

        request.setStatus("DECLINED");
        joinRequestRepository.save(request);
    }

    // =================================================================================
    // ============================ EMAIL INVITATION FIX ===============================
    // =================================================================================

    @Override
    @Transactional
    public void inviteUserByEmail(Long groupId, String email) {
        log.info("Inviting user {} to group {}", email, groupId);

        ProjectGroup group = findGroupOrThrow(groupId);
        User invitee = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No student found with email: " + email));

        // 1. Check if already a member
        if (memberRepository.findByGroupIdAndUserId(groupId, invitee.getId()).isPresent()) {
            throw new ValidationException("Student is already a member of this team.");
        }

        // 2. Check if team is full
        int currentMemberCount = memberRepository.countByGroupId(groupId);
        if (currentMemberCount >= group.getMaxMembers()) {
            throw new ValidationException("Cannot invite user, this team is already full.");
        }

        // 3. Fix applied here: Check for duplicates using the updated repository method
        if (groupInvitationRepository.existsByGroupIdAndInviteeIdAndStatus(groupId, invitee.getId(), InvitationStatus.PENDING)) {
            throw new ValidationException("An invitation has already been sent to this student.");
        }

        // 4. Save Invitation
        GroupInvitation invitation = new GroupInvitation();
        invitation.setGroup(group);
        invitation.setInvitee(invitee);
        invitation.setInviter(group.getOwner());
        invitation.setStatus(InvitationStatus.PENDING);

        groupInvitationRepository.save(invitation);
        log.info("Successfully sent invitation to {}", email);
    }

    // ─────────────────────── Helpers ─────────────────────────────────────────

    private ProjectGroup findGroupOrThrow(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", groupId));
    }

    private void assertOwner(ProjectGroup group, Long userId) {
        if (group.getOwner() == null || !group.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Only the group owner can perform this action");
        }
    }

    private GroupResponse mapToResponse(ProjectGroup group, int memberCount) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .maxMembers(group.getMaxMembers())
                .currentMembers(memberCount)
                .requiredSkills(group.getRequiredSkills() != null ? group.getRequiredSkills().toArray(new String[0]) : new String[0])
                .subject(group.getSubject())
                .open(group.isOpen())
                .ownerId(group.getOwner() != null ? group.getOwner().getId() : null)
                .ownerName(group.getOwner() != null ? group.getOwner().getFullName() : "Unknown")
                .createdAt(null)
                .yearOfStudy(group.getYearOfStudy())
                .semester(group.getSemester())
                .build();
    }
}