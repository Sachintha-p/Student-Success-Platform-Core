package com.sliit.studentplatform.auth.service.impl;

import com.sliit.studentplatform.auth.dto.request.UpdateStudentProfileRequest;
import com.sliit.studentplatform.auth.dto.response.UserProfileResponse;
import com.sliit.studentplatform.auth.entity.Student;
import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.StudentRepository;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.auth.service.interfaces.IStudentService;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements IStudentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentProfile(Long userId) {
        log.info("Fetching profile for user id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Student student = studentRepository.findByUserId(userId).orElse(null);

        return mapToResponse(user, student);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateStudentProfileRequest request) {
        log.info("Updating profile for user id: {}", userId);

        // 1. Find the User and update base details
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setFullName(request.getFullName());
        userRepository.save(user);

        // 2. Find the linked Student profile (or create one if it doesn't exist yet)
        Student student = studentRepository.findByUserId(userId).orElse(new Student());

        if (student.getId() == null) {
            student.setUser(user);
            // Note: Registration number usually comes from the initial auth/signup flow,
            // so we assume it's already set or handled elsewhere.
        }

        // 3. Update the specific student fields
        student.setDegreeProgramme(request.getDegreeProgramme());
        student.setYearOfStudy(request.getYearOfStudy());
        student.setRegistrationNumber(request.getRegistrationNumber()); // <-- ADDED THIS
        student.setGpa(request.getGpa());
        student.setSemester(request.getSemester());
        student.setBio(request.getBio());
        student.setSkills(request.getSkills());
        student.setProfilePictureUrl(request.getProfilePictureUrl());

        studentRepository.save(student);

        return mapToResponse(user, student);
    }

    // Helper method to map the Entities to the DTO
    private UserProfileResponse mapToResponse(User user, Student student) {
        UserProfileResponse response = UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();

        if (student != null) {
            response.setRegistrationNumber(student.getRegistrationNumber());
            response.setDegreeProgramme(student.getDegreeProgramme());
            response.setYearOfStudy(student.getYearOfStudy());
            response.setSemester(student.getSemester());
            response.setGpa(student.getGpa());
            response.setSkills(student.getSkills());
            response.setBio(student.getBio());
            response.setProfilePictureUrl(student.getProfilePictureUrl());
        }

        return response;
    }
}