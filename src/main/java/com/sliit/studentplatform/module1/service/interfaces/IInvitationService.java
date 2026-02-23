package com.sliit.studentplatform.module1.service.interfaces;

import com.sliit.studentplatform.module1.dto.request.SendInvitationRequest;
import com.sliit.studentplatform.module1.dto.response.InvitationResponse;

import java.util.List;

/** Service contract for invitation management (Single Responsibility). */
public interface IInvitationService {

  InvitationResponse sendInvitation(SendInvitationRequest request, Long inviterId);

  InvitationResponse acceptInvitation(Long invitationId, Long userId);

  InvitationResponse rejectInvitation(Long invitationId, Long userId);

  List<InvitationResponse> getPendingInvitationsForUser(Long userId);

  List<InvitationResponse> getInvitationsByGroup(Long groupId, Long requesterId);
}
