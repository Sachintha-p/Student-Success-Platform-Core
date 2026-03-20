package com.sliit.studentplatform.module1.repository;

import com.sliit.studentplatform.module1.entity.ProjectGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repository for {@link ProjectGroup} queries. */
@Repository
public interface ProjectGroupRepository extends JpaRepository<ProjectGroup, Long> {

    Page<ProjectGroup> findByOwnerId(Long ownerId, Pageable pageable);

    Page<ProjectGroup> findByOpenTrue(Pageable pageable);

    @Query("SELECT g FROM ProjectGroup g WHERE " +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(g.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ProjectGroup> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(m) FROM GroupMember m WHERE m.group.id = :groupId")
    int countMembersInGroup(@Param("groupId") Long groupId);

    // --- YOUR ADDED METHOD (For GroupService CRUD) ---
    List<ProjectGroup> findByLeaderId(Long leaderId);
}