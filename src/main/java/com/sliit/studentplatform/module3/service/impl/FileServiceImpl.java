package com.sliit.studentplatform.module3.service.impl;

import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module1.repository.ProjectGroupRepository;
import com.sliit.studentplatform.module3.entity.SharedFile;
import com.sliit.studentplatform.module3.repository.SharedFileRepository;
import com.sliit.studentplatform.module3.service.interfaces.IFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements IFileService {

  private final SharedFileRepository fileRepository;
  private final ProjectGroupRepository groupRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public SharedFile shareFile(Long groupId, Long userId, String fileName, String fileUrl,
      Long fileSize, String contentType, String description) {
    var group = groupRepository.findById(groupId)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectGroup", "id", groupId));
    var user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    return fileRepository.save(SharedFile.builder()
        .group(group).uploadedBy(user).fileName(fileName).fileUrl(fileUrl)
        .fileSize(fileSize).contentType(contentType).description(description).build());
  }

  @Override
  @Transactional(readOnly = true)
  public List<SharedFile> getFilesForGroup(Long groupId) {
    return fileRepository.findByGroupId(groupId);
  }

  @Override
  @Transactional
  public void deleteFile(Long fileId, Long userId) {
    fileRepository.deleteById(fileId);
  }
}
