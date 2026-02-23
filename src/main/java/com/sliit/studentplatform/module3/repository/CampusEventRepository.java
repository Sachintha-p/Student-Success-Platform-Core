package com.sliit.studentplatform.module3.repository;

import com.sliit.studentplatform.module3.entity.CampusEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampusEventRepository extends JpaRepository<CampusEvent, Long> {
  Page<CampusEvent> findByPublishedTrue(Pageable pageable);

  Page<CampusEvent> findByOrganizerId(Long organizerId, Pageable pageable);
}
