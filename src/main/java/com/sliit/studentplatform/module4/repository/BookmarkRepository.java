package com.sliit.studentplatform.module4.repository;

import com.sliit.studentplatform.module4.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
  List<Bookmark> findByUserId(Long userId);

  boolean existsByUserIdAndResourceId(Long userId, Long resourceId);

  void deleteByResourceId(Long resourceId);
}
