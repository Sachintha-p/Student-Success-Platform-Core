package com.sliit.studentplatform.module1.service.interfaces;

import com.sliit.studentplatform.module1.dto.response.InvitationResponse;
import java.util.List;

public interface IInvitationService {
    List<InvitationResponse> getMyPendingInvitations(Long userId);
    void acceptInvitation(Long inviteId, Long userId);
    void declineInvitation(Long inviteId, Long userId);
}