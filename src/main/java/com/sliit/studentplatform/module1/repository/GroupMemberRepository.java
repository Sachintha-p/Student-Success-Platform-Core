package com.sliit.studentplatform.module1.repository;

import com.sliit.studentplatform.module1.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

  List<GroupMember> findByGroupId(Long groupId);

  List<GroupMember> findByUserId(Long userId);

  Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

  boolean existsByGroupIdAndUserId(Long groupId, Long userId);

  int countByGroupId(Long groupId);
}
