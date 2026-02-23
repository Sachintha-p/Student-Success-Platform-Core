package com.sliit.studentplatform.auth.repository;

import com.sliit.studentplatform.auth.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for {@link Student} lookups. */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

  Optional<Student> findByUserId(Long userId);

  Optional<Student> findByRegistrationNumber(String registrationNumber);

  boolean existsByRegistrationNumber(String registrationNumber);
}
