package com.sliit.studentplatform.module1.service.interfaces;

import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module1.dto.request.CreateGroupRequest;
import com.sliit.studentplatform.module1.dto.response.GroupResponse;
import org.springframework.data.domain.Pageable;

/** Service contract for project group management (Single Responsibility). */
public interface ITeamService {

  GroupResponse createGroup(CreateGroupRequest request, Long creatorId);

  GroupResponse getGroupById(Long groupId);

  PagedResponse<GroupResponse> listOpenGroups(Pageable pageable);

  PagedResponse<GroupResponse> searchGroups(String keyword, Pageable pageable);

  GroupResponse updateGroup(Long groupId, CreateGroupRequest request, Long currentUserId);

  void deleteGroup(Long groupId, Long currentUserId);

  void leaveGroup(Long groupId, Long userId);
}
