package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
  List<Meeting> findByGroupId(Long groupId);

  List<Meeting> findByGroupIdAndStatus(Long groupId, String status);
}
