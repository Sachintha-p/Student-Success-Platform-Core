package com.sliit.studentplatform.module1.service.interfaces;

import com.sliit.studentplatform.module1.dto.response.MatchScoreResponse;

import java.util.List;

/**
 * Service contract for AI-powered student-to-group matching (Single
 * Responsibility).
 */
public interface IMatchingService {

  /** Returns a ranked list of groups best matching a student's skills. */
  List<MatchScoreResponse> findCompatibleGroupsForStudent(Long studentId);

  /**
   * Returns a ranked list of students best matching a group's required skills.
   */
  List<MatchScoreResponse> findCompatibleStudentsForGroup(Long groupId);

  /** Calculates a single match score between a student and a group. */
  MatchScoreResponse calculateMatchScore(Long studentId, Long groupId);
}
