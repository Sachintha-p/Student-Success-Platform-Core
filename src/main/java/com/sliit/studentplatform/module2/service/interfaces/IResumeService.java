package com.sliit.studentplatform.module2.service.interfaces;

import com.sliit.studentplatform.module2.dto.response.ResumeResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface IResumeService {
  ResumeResponse uploadResume(MultipartFile file, Long userId);

  List<ResumeResponse> getMyResumes(Long userId);

  ResumeResponse getResumeById(Long resumeId, Long userId);

  void deleteResume(Long resumeId, Long userId);

  ResumeResponse setPrimaryResume(Long resumeId, Long userId);
}
