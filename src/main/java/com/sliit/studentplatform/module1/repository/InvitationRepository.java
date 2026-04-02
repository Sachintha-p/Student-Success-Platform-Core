package com.sliit.studentplatform.module1.repository;

import com.sliit.studentplatform.module1.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    // Finds all pending invitations for a specific user
    List<Invitation> findByInviteeIdAndStatus(Long inviteeId, String status);

    // Checks if a specific invitation already exists to prevent spamming
    Optional<Invitation> findByGroupIdAndInviteeIdAndStatus(Long groupId, Long inviteeId, String status);
}