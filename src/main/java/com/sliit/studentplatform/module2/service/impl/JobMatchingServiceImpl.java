package com.sliit.studentplatform.module2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobMatchingServiceImpl implements IJobMatchingService {

  private final JobMatchRepository jobMatchRepository;
  private final JobListingRepository jobListingRepository;
  private final StudentRepository studentRepository;
  private final PlatformTransactionManager transactionManager;

  // AI Components
  private final OpenAiChatModel chatModel;
  private final ObjectMapper objectMapper;

  // Each save gets its own transaction — one failure won't poison the rest
  private TransactionTemplate txTemplate;

  @PostConstruct
  public void init() {
    txTemplate = new TransactionTemplate(transactionManager);
    txTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
  }

  @Override
  @Transactional(readOnly = true)
  public List<JobMatchResponse> findMatchingJobsForUser(Long userId) {
    return jobMatchRepository.findByUserIdOrderByMatchScoreDesc(userId).stream()
            .map(m -> {
              String safeJobType = m.getJobListing().getType() != null
                      ? String.valueOf(m.getJobListing().getType())
                      : "FULL_TIME";

              return JobMatchResponse.builder()
                      .id(m.getId())
                      .jobListingId(m.getJobListing().getId())
                      .jobTitle(m.getJobListing().getTitle())
                      .companyName(m.getJobListing().getCompany())
                      .location(m.getJobListing().getLocation())
                      .type(safeJobType)
                      .matchScore(Double.valueOf(Math.round(m.getMatchScore() * 10.0) / 10.0))
                      .matchedSkills(m.getMatchedSkills() != null ? m.getMatchedSkills() : new String[0])
                      .missingSkills(m.getMissingSkills() != null ? m.getMissingSkills() : new String[0])
                      .build();
            })
            .collect(Collectors.toList());
  }

  @Override
  // NOTE: NOT @Transactional here — we manage transactions manually per-save below
  public void refreshMatchesForUser(Long userId) {
    log.info("🤖 Triggering Full-Profile Semantic AI Matchmaker for user: {}", userId);

    Student student = studentRepository.findByUserId(userId).orElse(null);
    if (student == null) {
      log.warn("⚠️ Student not found for userId: {}. Cannot generate AI recommendations.", userId);
      return;
    }

    // 1. Delete old matches in its own transaction
    try {
      txTemplate.execute(status -> {
        jobMatchRepository.deleteByUserId(userId);
        return null;
      });
      log.info("🗑️ Cleared old matches for userId: {}", userId);
    } catch (Exception e) {
      log.error("⚠️ Failed to clear old matches for userId {}: {}", userId, e.getMessage());
      return;
    }

    // 2. Fetch all jobs & build student profile
    List<JobListing> allJobs = jobListingRepository.findAll();

    if (allJobs.isEmpty()) {
      log.warn("⚠️ No job listings found in database. Nothing to match.");
      return;
    }

    String degree = student.getDegreeProgramme() != null ? student.getDegreeProgramme() : "Not specified";
    String bio = student.getBio() != null ? student.getBio() : "Not specified";
    String studentSkills = student.getSkills() != null && student.getSkills().length > 0
            ? String.join(", ", student.getSkills())
            : "None listed";

    log.info("📋 Student Profile — Degree: {}, Skills: {}", degree, studentSkills);
    log.info("📦 Total jobs to evaluate: {}", allJobs.size());

    int matchedCount = 0;
    int failedCount = 0;

    // 3. Evaluate each job — each save is an independent transaction
    for (JobListing job : allJobs) {
      if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
        log.warn("⚠️ Skipping job with null/empty title (id: {})", job.getId());
        continue;
      }

      String jobSkills = job.getRequiredSkills() != null && job.getRequiredSkills().length > 0
              ? String.join(", ", job.getRequiredSkills())
              : "No specific skills listed by employer";

      String prompt = String.format(
              "You are an expert AI Tech Recruiter. Evaluate the match between a candidate and a job role.\n\n" +
                      "CANDIDATE PROFILE:\n" +
                      "- Degree: %s\n" +
                      "- Bio: %s\n" +
                      "- Technical/Soft Skills: %s\n\n" +
                      "JOB POSTING:\n" +
                      "- Job Title: '%s'\n" +
                      "- Required Skills: %s\n\n" +
                      "Analyze the semantic overlap between the candidate's entire profile and the job posting (Title + Required Skills).\n" +
                      "If 'Required Skills' are missing, judge fit primarily on how well their Degree/Bio/Skills fit the Job Title.\n" +
                      "Respond ONLY with a valid JSON object (no markdown, no extra text) with these exact keys:\n" +
                      "- \"matchScore\": integer between 0 and 100\n" +
                      "- \"matchedSkills\": array of strings (Maximum 3 brief reasons/skills they match)\n" +
                      "- \"missingSkills\": array of strings (Maximum 2 gaps)",
              degree, bio, studentSkills, job.getTitle(), jobSkills
      );

      try {
        // Call OpenAI — outside any transaction, no DB work here
        String aiResponse = chatModel.call(prompt);
        aiResponse = aiResponse.replaceAll("```json", "").replaceAll("```", "").trim();

        Map<String, Object> responseMap = objectMapper.readValue(aiResponse, Map.class);
        double score = ((Number) responseMap.get("matchScore")).doubleValue();

        if (score >= 60.0) {
          List<String> matched = (List<String>) responseMap.get("matchedSkills");
          List<String> missing = (List<String>) responseMap.get("missingSkills");
          final double finalScore = score;
          final String jobTitle = job.getTitle();

          // ✅ Each save runs in its own fresh transaction
          // A constraint violation here will NOT affect saves for other jobs
          try {
            txTemplate.execute(status -> {
              JobMatch match = JobMatch.builder()
                      .user(student.getUser())
                      .jobListing(job)
                      .matchScore(finalScore)
                      .matchedSkills(matched != null ? matched.toArray(new String[0]) : new String[0])
                      .missingSkills(missing != null ? missing.toArray(new String[0]) : new String[0])
                      .build();
              jobMatchRepository.save(match);
              return null;
            });
            matchedCount++;
            log.info("✅ Matched — '{}' (Score: {}%)", jobTitle, finalScore);
          } catch (Exception saveEx) {
            failedCount++;
            log.error("⚠️ Failed to save match for job '{}': {}", jobTitle, saveEx.getMessage());
          }

        } else {
          log.info("❌ Below threshold — '{}' (Score: {}%)", job.getTitle(), score);
        }

      } catch (Exception e) {
        failedCount++;
        log.error("⚠️ AI call failed for job '{}': {} — {}",
                job.getTitle(), e.getClass().getSimpleName(), e.getMessage());
      }
    }

    log.info("🎉 Matchmaking complete for userId: {} | Matched: {} | Failed: {}",
            userId, matchedCount, failedCount);
  }
}