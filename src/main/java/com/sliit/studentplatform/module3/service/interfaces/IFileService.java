package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.module3.entity.SharedFile;
import java.util.List;

public interface IFileService {
  SharedFile shareFile(Long groupId, Long userId, String fileName, String fileUrl, Long fileSize, String contentType,
      String description);

  List<SharedFile> getFilesForGroup(Long groupId);

  void deleteFile(Long fileId, Long userId);
}
