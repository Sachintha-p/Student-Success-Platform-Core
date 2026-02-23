package com.sliit.studentplatform.module1.service.interfaces;

import com.sliit.studentplatform.module1.dto.request.JoinRequestDto;
import com.sliit.studentplatform.module1.dto.response.InvitationResponse;

import java.util.List;

/** Service contract for join request management (Single Responsibility). */
public interface IJoinRequestService {

  InvitationResponse submitJoinRequest(JoinRequestDto request, Long requesterId);

  InvitationResponse approveJoinRequest(Long requestId, Long approverId);

  InvitationResponse rejectJoinRequest(Long requestId, Long approverId);

  List<InvitationResponse> getPendingRequestsForGroup(Long groupId, Long currentUserId);

  List<InvitationResponse> getMyJoinRequests(Long userId);
}
