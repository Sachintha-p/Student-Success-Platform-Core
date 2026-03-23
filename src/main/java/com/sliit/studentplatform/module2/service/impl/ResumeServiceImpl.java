package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module2.dto.response.ResumeResponse;
import com.sliit.studentplatform.module2.entity.Resume;
import com.sliit.studentplatform.module2.repository.ResumeRepository;
import com.sliit.studentplatform.module2.service.interfaces.IResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IResumeService}.
 *
 * <p>
 * TODO: Integrate cloud storage (S3 / GCS) for actual file upload in
 * production.
 * Currently stores metadata only; file URL must be set after upload.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements IResumeService {

  private final ResumeRepository resumeRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ResumeResponse uploadResume(MultipartFile file, Long userId) {
    log.info("Uploading resume for user id: {}", userId);

    var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    // 1. Define the local folder path (creates a folder for each user)
    String uploadDir = "uploads/resumes/" + userId + "/";
    File directory = new File(uploadDir);
    if (!directory.exists()) {
      directory.mkdirs(); // Creates the folders if they don't exist yet!
    }

    // 2. Clean the file name (removes dangerous characters)
    String fileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
    java.nio.file.Path targetLocation = java.nio.file.Paths.get(uploadDir + fileName);

    // 3. Save the actual file to your computer's hard drive!
    try {
      java.nio.file.Files.copy(file.getInputStream(), targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    } catch (java.io.IOException ex) {
      log.error("Failed to store file {}", fileName, ex);
      throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
    }

    // 4. Save the local absolute path as the URL so you can find it later
    String fileUrl = targetLocation.toAbsolutePath().toString();

    Resume resume = Resume.builder()
            .user(user)
            .fileName(fileName)
            .fileUrl(fileUrl) // Now stores the local Windows/Mac path!
            .fileSize(file.getSize())
            .contentType(file.getContentType())
            .primary(resumeRepository.findByUserId(userId).isEmpty())
            .build();

    resume = resumeRepository.save(resume);
    log.info("Resume saved locally with id: {} at path: {}", resume.getId(), fileUrl);
    return mapToResponse(resume);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ResumeResponse> getMyResumes(Long userId) {
    log.info("Fetching resumes for user id: {}", userId);
    return resumeRepository.findByUserId(userId).stream()
        .map(this::mapToResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public ResumeResponse getResumeById(Long resumeId, Long userId) {
    log.info("Fetching resume id: {} for user: {}", resumeId, userId);
    Resume resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", resumeId));
    return mapToResponse(resume);
  }

  @Override
  @Transactional
  public void deleteResume(Long resumeId, Long userId) {
    log.info("Deleting resume id: {} for user: {}", resumeId, userId);
    // TODO: delete file from cloud storage
    resumeRepository.deleteById(resumeId);
  }

  @Override
  @Transactional
  public ResumeResponse setPrimaryResume(Long resumeId, Long userId) {
    log.info("Setting primary resume id: {} for user: {}", resumeId, userId);
    // Unset current primary
    resumeRepository.findByUserIdAndPrimaryTrue(userId)
        .ifPresent(r -> {
          r.setPrimary(false);
          resumeRepository.save(r);
        });

    Resume resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", resumeId));
    resume.setPrimary(true);
    return mapToResponse(resumeRepository.save(resume));
  }

  private ResumeResponse mapToResponse(Resume r) {
    return ResumeResponse.builder()
        .id(r.getId()).userId(r.getUser().getId())
        .fileName(r.getFileName()).fileUrl(r.getFileUrl())
        .fileSize(r.getFileSize()).primary(r.isPrimary())
        .createdAt(r.getCreatedAt()).build();
  }
}
