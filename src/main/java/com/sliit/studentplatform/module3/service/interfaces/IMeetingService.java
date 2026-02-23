package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.module3.dto.request.CreateMeetingRequest;
import com.sliit.studentplatform.module3.dto.response.MeetingResponse;
import java.util.List;

public interface IMeetingService {
  MeetingResponse createMeeting(CreateMeetingRequest request, Long userId);

  MeetingResponse getMeeting(Long id);

  List<MeetingResponse> getMeetingsForGroup(Long groupId);

  MeetingResponse updateMeetingStatus(Long id, String status, Long userId);
}
