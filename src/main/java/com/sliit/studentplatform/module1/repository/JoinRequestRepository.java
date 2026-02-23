package com.sliit.studentplatform.module1.repository;

import com.sliit.studentplatform.common.enums.Status;
import com.sliit.studentplatform.module1.entity.JoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {

  List<JoinRequest> findByGroupIdAndStatus(Long groupId, Status status);

  List<JoinRequest> findByRequesterId(Long requesterId);

  boolean existsByGroupIdAndRequesterIdAndStatus(Long groupId, Long requesterId, Status status);
}
