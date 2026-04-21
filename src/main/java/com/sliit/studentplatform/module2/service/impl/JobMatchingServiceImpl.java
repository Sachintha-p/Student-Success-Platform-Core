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
import org.springframework.transaction.annotation.Transactional;

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

  // AI Components
  private final OpenAiChatModel chatModel;
  private final ObjectMapper objectMapper;

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
  @Transactional
  public void refreshMatchesForUser(Long userId) {
    log.info("🤖 Triggering Full-Profile Semantic AI Matchmaker for user: {}", userId);

    Student student = studentRepository.findByUserId(userId).orElse(null);
    if (student == null) {
      log.warn("Student not found. Cannot generate AI recommendations.");
      return;
    }

    // 1. Clean up old matches
    jobMatchRepository.deleteByUserId(userId);

    // 2. Fetch All Jobs & Prepare Student Profile Data
    List<JobListing> allJobs = jobListingRepository.findAll();

    String degree = student.getDegreeProgramme() != null ? student.getDegreeProgramme() : "Not specified";
    String bio = student.getBio() != null ? student.getBio() : "Not specified";
    String studentSkills = student.getSkills() != null && student.getSkills().length > 0
            ? String.join(", ", student.getSkills())
            : "None listed";

    // 3. Loop through jobs and evaluate EVERYTHING (Semantic Title + Skill Overlap)
    for (JobListing job : allJobs) {
      if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
        continue; // Skip invalid jobs
      }

      // Safely handle job skills (if the admin provided them)
      String jobSkills = job.getRequiredSkills() != null && job.getRequiredSkills().length > 0
              ? String.join(", ", job.getRequiredSkills())
              : "No specific skills listed by employer";

      // 🧠 THE ULTIMATE HYBRID AI PROMPT
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
                      "- \"matchedSkills\": array of strings (Maximum 3 brief reasons/skills they match, e.g., 'Strong React background', 'Fits Software Engineer title')\n" +
                      "- \"missingSkills\": array of strings (Maximum 2 gaps, e.g., 'Missing Python', 'No leadership experience mentioned')",
              degree, bio, studentSkills, job.getTitle(), jobSkills
      );

      try {
        // Call OpenAI
        String aiResponse = chatModel.call(prompt);

        // Clean up markdown
        aiResponse = aiResponse.replaceAll("```json", "").replaceAll("```", "").trim();

        // Parse JSON
        Map<String, Object> responseMap = objectMapper.readValue(aiResponse, Map.class);

        // Extract Score
        double score = ((Number) responseMap.get("matchScore")).doubleValue();

        // Save if 60% or better!
        if (score >= 60.0) {
          List<String> matched = (List<String>) responseMap.get("matchedSkills");
          List<String> missing = (List<String>) responseMap.get("missingSkills");

          JobMatch match = JobMatch.builder()
                  .user(student.getUser())
                  .jobListing(job)
                  .matchScore(score)
                  .matchedSkills(matched != null ? matched.toArray(new String[0]) : new String[0])
                  .missingSkills(missing != null ? missing.toArray(new String[0]) : new String[0])
                  .build();

          jobMatchRepository.save(match);
          log.info("✅ AI matched job: {} (Score: {}%)", job.getTitle(), score);
        } else {
          log.info("❌ AI rejected job: {} (Score: {}%)", job.getTitle(), score);
        }

      } catch (Exception e) {
        log.error("⚠️ AI Matchmaking failed for job {}: {}", job.getTitle(), e.getMessage());
      }
    }

    log.info("🎉 Hybrid Semantic Matchmaking sequence completed for user: {}", userId);
  }
}