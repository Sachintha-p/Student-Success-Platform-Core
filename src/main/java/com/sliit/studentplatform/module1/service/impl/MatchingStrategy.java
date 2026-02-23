package com.sliit.studentplatform.module1.service.impl;

import com.sliit.studentplatform.module1.dto.response.MatchScoreResponse;

/**
 * Strategy interface for team-matching algorithms.
 *
 * <p>
 * Applying the Open/Closed Principle — new matching strategies (e.g. GPA-based,
 * mixed-skills) can be added without modifying existing code.
 */
public interface MatchingStrategy {

  /**
   * Calculates a compatibility score between a student and a group.
   *
   * @param studentSkills  skills the student has
   * @param requiredSkills skills the group requires
   * @param studentId      database ID of the student
   * @param groupId        database ID of the group
   * @return a {@link MatchScoreResponse} with score details
   */
  MatchScoreResponse calculate(String[] studentSkills, String[] requiredSkills,
      Long studentId, Long groupId);
}
