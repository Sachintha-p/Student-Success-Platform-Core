package com.sliit.studentplatform.module1.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.module1.dto.GroupRequest;
import com.sliit.studentplatform.module1.dto.GroupResponse;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final ProjectGroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupResponse createGroup(GroupRequest request, Long leaderId) {
        User owner = userRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Use standard instantiation since ProjectGroup doesn't have @Builder
        ProjectGroup group = new ProjectGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());
        group.setOwner(owner);
        group.setOpen(true);

        ProjectGroup savedGroup = groupRepository.save(group);
        return mapToResponse(savedGroup);
    }

    public GroupResponse updateGroup(Long groupId, GroupRequest request, Long requesterId) {
        ProjectGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group.getOwner() == null || !group.getOwner().getId().equals(requesterId)) {
            throw new RuntimeException("You are not authorized to edit this group");
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());

        ProjectGroup updatedGroup = groupRepository.save(group);
        return mapToResponse(updatedGroup);
    }

    public void deleteGroup(Long groupId, Long requesterId) {
        ProjectGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group.getOwner() == null || !group.getOwner().getId().equals(requesterId)) {
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
                .leaderId(group.getOwner() != null ? group.getOwner().getId() : null)
                .build();
    }
}