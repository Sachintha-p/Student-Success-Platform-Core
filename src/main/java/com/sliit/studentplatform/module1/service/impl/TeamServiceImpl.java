package com.sliit.studentplatform.module1.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.StudentRepository;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ConflictException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.common.exception.ValidationException;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module1.dto.request.CreateGroupRequest;
import com.sliit.studentplatform.module1.dto.response.GroupResponse;
import com.sliit.studentplatform.module1.entity.GroupMember;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import com.sliit.studentplatform.module1.repository.GroupMemberRepository;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module1.service.interfaces.ITeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link ITeamService}.
 *
 * <p>
 * Handles ONLY group management operations (Single Responsibility Principle).
 * Invitations → {@code InvitationServiceImpl}. Matching →
 * {@code MatchingServiceImpl}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements ITeamService {

    private final ProjectGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserRepository userRepository;

    private static final int MAX_GROUPS_PER_USER = 5;

    // ─────────────────────── Create Group ────────────────────────────────────

    @Override
    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request, Long creatorId) {
        log.info("Creating group '{}' for user id: {}", request.getName(), creatorId);

        // Validate creator exists
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId));

        // Business rule: a user cannot be in more groups as owner than MAX_GROUPS_PER_USER
        long ownedGroups = groupRepository.findByOwnerId(creatorId, Pageable.unpaged()).getTotalElements();
        if (ownedGroups >= MAX_GROUPS_PER_USER) {
            throw new ValidationException("You cannot own more than " + MAX_GROUPS_PER_USER + " groups");
        }

        // Build and persist group (NEW: Added Year and Semester)
        ProjectGroup group = ProjectGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .maxMembers(request.getMaxMembers())
                .requiredSkills(request.getRequiredSkills())
                .subject(request.getSubject())
                .yearOfStudy(request.getYearOfStudy())
                .semester(request.getSemester())
                .owner(creator)
                .open(true)
                .build();
        group = groupRepository.save(group);

        // Add creator as a leader member
        GroupMember leaderMembership = GroupMember.builder()
                .group(group)
                .user(creator)
                .leader(true)
                .build();
        memberRepository.save(leaderMembership);

        log.info("Group created with id: {}", group.getId());
        return mapToResponse(group, 1);
    }

    // ─────────────────────── Get Group ───────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long groupId) {
        log.info("Fetching group id: {}", groupId);
        ProjectGroup group = findGroupOrThrow(groupId);
        int memberCount = memberRepository.countByGroupId(groupId);
        return mapToResponse(group, memberCount);
    }

    // ─────────────────────── List & Search ───────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<GroupResponse> listOpenGroups(Pageable pageable) {
        log.info("Listing open groups, page: {}", pageable.getPageNumber());
        Page<ProjectGroup> page = groupRepository.findByOpenTrue(pageable);
        return PagedResponse.of(page.map(g -> mapToResponse(g, memberRepository.countByGroupId(g.getId()))));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<GroupResponse> searchGroups(String keyword, Pageable pageable) {
        log.info("Searching groups with keyword: '{}'", keyword);
        Page<ProjectGroup> page = groupRepository.searchByKeyword(keyword, pageable);
        return PagedResponse.of(page.map(g -> mapToResponse(g, memberRepository.countByGroupId(g.getId()))));
    }

    // ─────────────────────── Update Group ────────────────────────────────────

    @Override
    @Transactional
    public GroupResponse updateGroup(Long groupId, CreateGroupRequest request, Long currentUserId) {
        log.info("Updating group id: {} by user: {}", groupId, currentUserId);
        ProjectGroup group = findGroupOrThrow(groupId);
        assertOwner(group, currentUserId);

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());
        group.setRequiredSkills(request.getRequiredSkills());
        group.setSubject(request.getSubject());

        // NEW: Allow updating year and semester
        group.setYearOfStudy(request.getYearOfStudy());
        group.setSemester(request.getSemester());

        group = groupRepository.save(group);
        return mapToResponse(group, memberRepository.countByGroupId(groupId));
    }

    // ─────────────────────── Delete Group ────────────────────────────────────

    @Override
    @Transactional
    public void deleteGroup(Long groupId, Long currentUserId) {
        log.info("Deleting group id: {} by user: {}", groupId, currentUserId);
        ProjectGroup group = findGroupOrThrow(groupId);
        assertOwner(group, currentUserId);
        groupRepository.delete(group);
    }

    // ─────────────────────── Leave Group ─────────────────────────────────────

    @Override
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        log.info("User {} leaving group {}", userId, groupId);
        GroupMember membership = memberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found for user " + userId));

        if (membership.isLeader()) {
            throw new ValidationException(
                    "Group leader cannot leave the group. Transfer ownership first or delete the group.");
        }
        memberRepository.delete(membership);
    }

    // ─────────────────────── Helpers ─────────────────────────────────────────

    private ProjectGroup findGroupOrThrow(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", groupId));
    }

    private void assertOwner(ProjectGroup group, Long userId) {
        if (!group.getOwner().getId().equals(userId)) {
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
                .requiredSkills(group.getRequiredSkills())
                .subject(group.getSubject())
                .open(group.isOpen())
                .ownerId(group.getOwner().getId())
                .ownerName(group.getOwner().getFullName())
                .createdAt(group.getCreatedAt())
                // NEW: Map these back to the response
                .yearOfStudy(group.getYearOfStudy())
                .semester(group.getSemester())
                .build();
    }

    // ─────────────────────── Join Group ──────────────────────────────────────

    @Override
    @Transactional
    public void joinGroup(Long groupId, Long userId) {
        ProjectGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!group.isOpen()) {
            throw new IllegalStateException("This group is currently closed to new members.");
        }

        if (group.getMembers() == null) {
            group.setMembers(new java.util.ArrayList<>());
        }

        if (group.getMembers().size() >= group.getMaxMembers()) {
            throw new IllegalStateException("This group is already full.");
        }

        boolean alreadyMember = group.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(userId));

        if (alreadyMember) {
            throw new IllegalStateException("You are already a member of this group.");
        }

        GroupMember newMember = GroupMember.builder()
                .group(group)
                .user(user)
                .build();

        group.getMembers().add(newMember);
        groupRepository.save(group);
    }

    // ─────────────────────── Get My Groups ─────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<GroupResponse> getMyGroups(Long userId, Pageable pageable) {
        log.info("Fetching joined groups for user id: {}", userId);
        Page<ProjectGroup> page = groupRepository.findByMembers_UserId(userId, pageable);
        return PagedResponse.of(page.map(g -> mapToResponse(g, memberRepository.countByGroupId(g.getId()))));
    }
}