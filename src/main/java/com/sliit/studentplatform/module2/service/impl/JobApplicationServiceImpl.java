package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ConflictException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module2.dto.request.JobApplicationRequest;
import com.sliit.studentplatform.module2.entity.JobApplication;
import com.sliit.studentplatform.module2.entity.Resume;
import com.sliit.studentplatform.module2.repository.JobApplicationRepository;
import com.sliit.studentplatform.module2.repository.JobListingRepository;
import com.sliit.studentplatform.module2.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Placeholder service for job applications — interfaces are defined in
 * IJobService as a TODO.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationServiceImpl {

  private final JobApplicationRepository applicationRepository;
  private final JobListingRepository jobListingRepository;
  private final ResumeRepository resumeRepository;
  private final UserRepository userRepository;

  @Transactional
  public JobApplication apply(JobApplicationRequest request, Long userId) {
    log.info("User {} applying to job {}", userId, request.getJobListingId());
    if (applicationRepository.existsByJobListingIdAndUserId(request.getJobListingId(), userId)) {
      throw new ConflictException("You have already applied to this job");
    }
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    var listing = jobListingRepository.findById(request.getJobListingId())
        .orElseThrow(() -> new ResourceNotFoundException("JobListing", "id", request.getJobListingId()));
    Resume resume = null;
    if (request.getResumeId() != null) {
      resume = resumeRepository.findById(request.getResumeId()).orElse(null);
    }
    return applicationRepository.save(JobApplication.builder()
        .jobListing(listing).user(user).resume(resume)
        .coverLetter(request.getCoverLetter()).build());
  }
}
