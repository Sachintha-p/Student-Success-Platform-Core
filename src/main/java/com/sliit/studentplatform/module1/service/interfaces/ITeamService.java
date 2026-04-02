package com.sliit.studentplatform.module1.service.interfaces;

import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module1.dto.request.CreateGroupRequest;
import com.sliit.studentplatform.module1.dto.response.GroupResponse;
import org.springframework.data.domain.Pageable;
import com.sliit.studentplatform.module1.dto.response.JoinRequestResponse;
import java.util.List;

/** Service contract for project group management (Single Responsibility). */
public interface ITeamService {

    GroupResponse createGroup(CreateGroupRequest request, Long creatorId);

    GroupResponse getGroupById(Long groupId);

    PagedResponse<GroupResponse> listOpenGroups(Pageable pageable);

    PagedResponse<GroupResponse> searchGroups(String keyword, Pageable pageable);

    GroupResponse updateGroup(Long groupId, CreateGroupRequest request, Long currentUserId);

    void deleteGroup(Long groupId, Long currentUserId);

    void leaveGroup(Long groupId, Long userId);

    void joinGroup(Long groupId, Long userId);
    void inviteUserByEmail(Long groupId, String email);

    // --- NEW: JOIN REQUEST METHODS ---
    List<JoinRequestResponse> getPendingJoinRequests(Long ownerId);
    void acceptJoinRequest(Long requestId, Long ownerId);
    void declineJoinRequest(Long requestId, Long ownerId);
    // --- NEW METHOD ADDED FOR 'MY TEAMS' MATCHMAKER FEATURE ---
    PagedResponse<GroupResponse> getMyGroups(Long userId, Pageable pageable);

}