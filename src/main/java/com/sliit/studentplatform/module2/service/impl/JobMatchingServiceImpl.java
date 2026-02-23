package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.auth.entity.Student;
import com.sliit.studentplatform.auth.repository.StudentRepository;
import com.sliit.studentplatform.module2.dto.response.JobMatchResponse;
import com.sliit.studentplatform.module2.entity.JobListing;
import com.sliit.studentplatform.module2.entity.JobMatch;
import com.sliit.studentplatform.module2.repository.JobListingRepository;
import com.sliit.studentplatform.module2.repository.JobMatchRepository;
import com.sliit.studentplatform.module2.service.interfaces.IJobMatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobMatchingServiceImpl implements IJobMatchingService {

  private final JobMatchRepository jobMatchRepository;
  private final JobListingRepository jobListingRepository;
  private final StudentRepository studentRepository;

  @Override
  @Transactional(readOnly = true)
  public List<JobMatchResponse> findMatchingJobsForUser(Long userId) {
    log.info("Finding matching jobs for user: {}", userId);
    return jobMatchRepository.findByUserIdOrderByMatchScoreDesc(userId)
        .stream().map(m -> JobMatchResponse.builder()
            .jobListingId(m.getJobListing().getId())
            .jobTitle(m.getJobListing().getTitle())
            .company(m.getJobListing().getCompany())
            .matchScore(m.getMatchScore())
            .matchedSkills(m.getMatchedSkills())
            .missingSkills(m.getMissingSkills())
            .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void refreshMatchesForUser(Long userId) {
    log.info("Refreshing job matches for user: {}", userId);
    // TODO: add scheduler to run this periodically for all students
    Student student = studentRepository.findByUserId(userId).orElse(null);
    if (student == null || student.getSkills() == null)
      return;

    List<JobListing> activeJobs = jobListingRepository.findByActiveTrue(Pageable.unpaged()).getContent();
    for (JobListing job : activeJobs) {
      if (job.getRequiredSkills() == null)
        continue;
      List<String> matched = new ArrayList<>();
      List<String> missing = new ArrayList<>();
      for (String skill : job.getRequiredSkills()) {
        if (Arrays.asList(student.getSkills()).contains(skill))
          matched.add(skill);
        else
          missing.add(skill);
      }
      double score = (double) matched.size() / job.getRequiredSkills().length * 100;
      JobMatch match = JobMatch.builder()
          .user(student.getUser()).jobListing(job)
          .matchScore(score)
          .matchedSkills(matched.toArray(new String[0]))
          .missingSkills(missing.toArray(new String[0]))
          .build();
      jobMatchRepository.save(match);
    }
  }
}
