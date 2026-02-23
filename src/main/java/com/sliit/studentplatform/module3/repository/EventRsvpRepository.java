package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.EventRsvp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRsvpRepository extends JpaRepository<EventRsvp, Long> {
  List<EventRsvp> findByEventId(Long eventId);

  boolean existsByEventIdAndUserId(Long eventId, Long userId);

  int countByEventId(Long eventId);
}
