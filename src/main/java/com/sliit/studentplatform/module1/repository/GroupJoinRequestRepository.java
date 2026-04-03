package com.sliit.studentplatform.module1.repository;

import com.sliit.studentplatform.module1.entity.GroupJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest, Long> {
    List<GroupJoinRequest> findByGroupOwnerIdAndStatus(Long ownerId, String status);
    boolean existsByGroupIdAndStudentIdAndStatus(Long groupId, Long studentId, String status);
}