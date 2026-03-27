package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.CampusEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampusEventRepository extends JpaRepository<CampusEvent, Long> {
    List<CampusEvent> findByCategory(String category);

    List<CampusEvent> findByEventDateAfter(LocalDateTime now);

    List<CampusEvent> findByCategoryAndEventDateAfter(String category, LocalDateTime now);
}
