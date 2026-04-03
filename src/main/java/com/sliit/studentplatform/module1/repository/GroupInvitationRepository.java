package com.sliit.studentplatform.module1.repository;

import com.sliit.studentplatform.module1.entity.GroupInvitation;
import com.sliit.studentplatform.module1.entity.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

    List<GroupInvitation> findByInviteeIdAndStatus(Long inviteeId, InvitationStatus status);

    Optional<GroupInvitation> findByIdAndInviteeId(Long id, Long inviteeId);

    // This is the correct method we will use to check for duplicates!
    boolean existsByGroupIdAndInviteeIdAndStatus(Long groupId, Long inviteeId, InvitationStatus status);
}