package com.sliit.studentplatform.module1.service;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.module1.dto.GroupRequest;
import com.sliit.studentplatform.module1.dto.GroupResponse;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final ProjectGroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupResponse createGroup(GroupRequest request, Long leaderId) {
        User owner = userRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProjectGroup group = ProjectGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .maxMembers(request.getMaxMembers())
                .leaderId(leaderId)
                .owner(owner)
                .open(true)
                // MANUALLY SETTING THESE TO FIX THE DATABASE ERROR:
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Also set the auditing fields from the parent class
        group.setCreatedBy(owner.getEmail());
        group.setUpdatedBy(owner.getEmail());

        ProjectGroup savedGroup = groupRepository.save(group);
        return mapToResponse(savedGroup);
    }

    public GroupResponse updateGroup(Long groupId, GroupRequest request, Long requesterId) {
        ProjectGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getLeaderId().equals(requesterId)) {
            throw new RuntimeException("You are not authorized to edit this group");
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());
        group.setUpdatedAt(LocalDateTime.now()); // Update the time manually

        ProjectGroup updatedGroup = groupRepository.save(group);
        return mapToResponse(updatedGroup);
    }

    public void deleteGroup(Long groupId, Long requesterId) {
        ProjectGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getLeaderId().equals(requesterId)) {
            throw new RuntimeException("You are not authorized to delete this group");
        }

        groupRepository.delete(group);
    }

    private GroupResponse mapToResponse(ProjectGroup group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .maxMembers(group.getMaxMembers())
                .leaderId(group.getLeaderId())
                .build();
    }
}