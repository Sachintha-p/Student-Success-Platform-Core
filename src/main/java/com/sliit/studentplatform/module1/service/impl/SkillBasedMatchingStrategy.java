package com.sliit.studentplatform.module1.service.impl;

import com.sliit.studentplatform.module1.dto.response.MatchScoreResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Skill-based implementation of {@link MatchingStrategy}.
 *
 * <p>
 * Score = (number of student skills that match required skills) /
 * (total required skills) * 100.
 *
 * <p>
 * This is the {@code @Primary} strategy — new strategies (e.g. GPA-based)
 * can be added without touching this class (Open/Closed Principle).
 */
@Component
public class SkillBasedMatchingStrategy implements MatchingStrategy {

  private static final int MAX_SCORE = 100;

  @Override
  public MatchScoreResponse calculate(String[] studentSkills, String[] requiredSkills,
      Long studentId, Long groupId) {

    if (requiredSkills == null || requiredSkills.length == 0) {
      // Group has no skill requirements — everyone matches perfectly
      return MatchScoreResponse.builder()
          .studentId(studentId)
          .groupId(groupId)
          .score(MAX_SCORE)
          .matchedSkills(new String[0])
          .missingSkills(new String[0])
          .build();
    }

    Set<String> studentSkillSet = studentSkills == null
        ? new HashSet<>()
        : Arrays.stream(studentSkills)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

    Set<String> matched = new HashSet<>();
    Set<String> missing = new HashSet<>();

    for (String req : requiredSkills) {
      if (studentSkillSet.contains(req.toLowerCase())) {
        matched.add(req);
      } else {
        missing.add(req);
      }
    }

    double score = ((double) matched.size() / requiredSkills.length) * MAX_SCORE;

    return MatchScoreResponse.builder()
        .studentId(studentId)
        .groupId(groupId)
        .score(Math.round(score * 10.0) / 10.0)
        .matchedSkills(matched.toArray(new String[0]))
        .missingSkills(missing.toArray(new String[0]))
        .build();
  }
}
