package com.sliit.studentplatform.module1.repository;

import com.sliit.studentplatform.common.enums.Status;
import com.sliit.studentplatform.module1.entity.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {

  List<TeamInvitation> findByInviteeIdAndStatus(Long inviteeId, Status status);

  List<TeamInvitation> findByGroupIdAndStatus(Long groupId, Status status);

  boolean existsByGroupIdAndInviteeIdAndStatus(Long groupId, Long inviteeId, Status status);
}
