package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.MeetingAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeetingAvailabilityRepository extends JpaRepository<MeetingAvailability, Long> {
  List<MeetingAvailability> findByMeetingId(Long meetingId);

  List<MeetingAvailability> findByUserId(Long userId);
}
