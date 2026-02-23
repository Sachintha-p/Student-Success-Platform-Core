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

    // TODO: Upload file to S3 and obtain file URL
    String fileUrl = "https://storage.example.com/resumes/" + userId + "/" + file.getOriginalFilename();

    Resume resume = Resume.builder()
        .user(user)
        .fileName(file.getOriginalFilename())
        .fileUrl(fileUrl)
        .fileSize(file.getSize())
        .contentType(file.getContentType())
        .primary(resumeRepository.findByUserId(userId).isEmpty())
        .build();

    resume = resumeRepository.save(resume);
    log.info("Resume saved with id: {}", resume.getId());
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
