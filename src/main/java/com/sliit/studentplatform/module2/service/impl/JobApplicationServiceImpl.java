package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.enums.Status;
import com.sliit.studentplatform.common.exception.ConflictException;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.module2.dto.request.JobApplicationRequest;
import com.sliit.studentplatform.module2.dto.response.JobApplicationResponse;
import com.sliit.studentplatform.module2.entity.JobApplication;
import com.sliit.studentplatform.module2.entity.Resume;
import com.sliit.studentplatform.module2.repository.JobApplicationRepository;
import com.sliit.studentplatform.module2.repository.JobListingRepository;
import com.sliit.studentplatform.module2.repository.ResumeRepository;
import com.sliit.studentplatform.module2.service.interfaces.IJobApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationServiceImpl implements IJobApplicationService {

  private final JobApplicationRepository applicationRepository;
  private final JobListingRepository jobListingRepository;
  private final ResumeRepository resumeRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public JobApplicationResponse apply(JobApplicationRequest request, Long userId) {
    if (applicationRepository.existsByJobListingIdAndUserId(request.getJobListingId(), userId)) {
      throw new ConflictException("You have already applied to this job!");
    }

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    var listing = jobListingRepository.findById(request.getJobListingId())
            .orElseThrow(() -> new ResourceNotFoundException("JobListing", "id", request.getJobListingId()));

    Resume resume = null;
    if (request.getResumeId() != null) {
      resume = resumeRepository.findById(request.getResumeId()).orElse(null);
    }

    JobApplication application = JobApplication.builder()
            .jobListing(listing)
            .user(user)
            .resume(resume)
            .fullName(request.getFullName()) // MAPPING NEW FIELD
            .email(request.getEmail())       // MAPPING NEW FIELD
            .phoneNumber(request.getPhoneNumber()) // MAPPING NEW FIELD
            .coverLetter(request.getCoverLetter())
            .status(Status.PENDING)
            .build();

    return mapToResponse(applicationRepository.save(application));
  }

  @Override
  public List<JobApplicationResponse> getMyApplications(Long userId) {
    return applicationRepository.findByUserId(userId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
  }

  @Override
  public List<JobApplicationResponse> getApplicationsForJob(Long jobListingId) {
    return applicationRepository.findByJobListingId(jobListingId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public JobApplicationResponse updateApplicationStatus(Long applicationId, Status status, String notes) {
    JobApplication application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

    application.setStatus(status);
    application.setRecruiterNotes(notes);
    return mapToResponse(applicationRepository.save(application));
  }

  @Override
  @Transactional
  public void withdrawApplication(Long applicationId, Long userId) {
    JobApplication application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

    if (!application.getUser().getId().equals(userId)) {
      throw new UnauthorizedException("You can only withdraw your own applications!");
    }
    applicationRepository.delete(application);
  }

  private JobApplicationResponse mapToResponse(JobApplication entity) {
    return JobApplicationResponse.builder()
            .id(entity.getId())
            .jobListingId(entity.getJobListing().getId())
            .jobTitle(entity.getJobListing().getTitle())
            .companyName(entity.getJobListing().getCompany())
            .userId(entity.getUser().getId())
            .applicantName(entity.getUser().getFullName())
            .resumeId(entity.getResume() != null ? entity.getResume().getId() : null)
            .resumeFileName(entity.getResume() != null ? entity.getResume().getFileName() : "No Resume Attached")
            .coverLetter(entity.getCoverLetter())
            .status(entity.getStatus())
            .recruiterNotes(entity.getRecruiterNotes())
            .appliedAt(entity.getCreatedAt())
            .build();
  }
}