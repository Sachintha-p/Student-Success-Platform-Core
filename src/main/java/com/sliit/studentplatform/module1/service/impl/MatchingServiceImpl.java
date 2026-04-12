package com.sliit.studentplatform.module1.service.impl;

import com.sliit.studentplatform.auth.entity.Student;
import com.sliit.studentplatform.auth.repository.StudentRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module1.dto.response.MatchScoreResponse;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module1.service.interfaces.IMatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IMatchingService}.
 *
 * <p>
 * Uses the injected {@link MatchingStrategy} (defaults to
 * {@link SkillBasedMatchingStrategy}). New strategies can be swapped in
 * without modifying this class (Open/Closed Principle + Dependency Inversion).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingServiceImpl implements IMatchingService {

    private final StudentRepository studentRepository;
    private final ProjectGroupRepository groupRepository;

    /**
     * Strategy is injected (defaults to SkillBasedMatchingStrategy via component
     * scan).
     */
    private final MatchingStrategy matchingStrategy;

    @Override
    @Transactional(readOnly = true)
    public List<MatchScoreResponse> findCompatibleGroupsForStudent(Long studentId) {
        log.info("Finding compatible groups for student id: {}", studentId);

        Student student = studentRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user id: " + studentId));

        List<ProjectGroup> openGroups = groupRepository.findByOpenTrue(Pageable.unpaged()).getContent();

        return openGroups.stream()
                .map(group -> {
                    // 🔧 FIX: Converted group.getRequiredSkills() from List to Array safely
                    MatchScoreResponse score = matchingStrategy.calculate(
                            student.getSkills(),
                            group.getRequiredSkills() != null ? group.getRequiredSkills().toArray(new String[0]) : new String[0],
                            studentId,
                            group.getId());
                    score.setGroupName(group.getName());
                    score.setStudentName(student.getUser().getFullName());
                    return score;
                })
                .filter(r -> r.getScore() > 0)
                .sorted(Comparator.comparingDouble(MatchScoreResponse::getScore).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchScoreResponse> findCompatibleStudentsForGroup(Long groupId) {
        log.info("Finding compatible students for group id: {}", groupId);

        ProjectGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", groupId));

        // TODO: paginate; for now load all students (acceptable for small cohorts)
        List<Student> allStudents = studentRepository.findAll();

        return allStudents.stream()
                .map(student -> {
                    // 🔧 FIX: Converted group.getRequiredSkills() from List to Array safely
                    MatchScoreResponse score = matchingStrategy.calculate(
                            student.getSkills(),
                            group.getRequiredSkills() != null ? group.getRequiredSkills().toArray(new String[0]) : new String[0],
                            student.getUser().getId(),
                            groupId);
                    score.setGroupName(group.getName());
                    score.setStudentName(student.getUser().getFullName());
                    return score;
                })
                .filter(r -> r.getScore() > 0)
                .sorted(Comparator.comparingDouble(MatchScoreResponse::getScore).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MatchScoreResponse calculateMatchScore(Long studentId, Long groupId) {
        log.info("Calculating match score: student {} vs group {}", studentId, groupId);

        Student student = studentRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for user id: " + studentId));
        ProjectGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", groupId));

        // 🔧 FIX: Converted group.getRequiredSkills() from List to Array safely
        MatchScoreResponse result = matchingStrategy.calculate(
                student.getSkills(),
                group.getRequiredSkills() != null ? group.getRequiredSkills().toArray(new String[0]) : new String[0],
                studentId,
                groupId);

        result.setStudentName(student.getUser().getFullName());
        result.setGroupName(group.getName());
        return result;
    }
}