package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.dto.response.AvailabilitySummaryResponse;
import com.sliit.studentplatform.module3.entity.MeetingAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingAvailabilityRepository extends JpaRepository<MeetingAvailability, Long> {
    Optional<MeetingAvailability> findByMeetingIdAndUserId(Long meetingId, Long userId);

    @Query("SELECT new com.sliit.studentplatform.module3.dto.response.AvailabilitySummaryResponse(d, COUNT(ma.id)) " +
           "FROM MeetingAvailability ma JOIN ma.availableDates d " +
           "WHERE ma.meeting.id = :meetingId " +
           "GROUP BY d")
    List<AvailabilitySummaryResponse> getSummaryByMeetingId(@Param("meetingId") Long meetingId);
}
