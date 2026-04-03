package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.module3.dto.request.AvailabilityRequest;
import com.sliit.studentplatform.module3.dto.request.CreateMeetingRequest;
import com.sliit.studentplatform.module3.dto.response.AvailabilitySummaryResponse;
import com.sliit.studentplatform.module3.dto.response.MeetingResponse;
import java.util.List;

public interface IMeetingService {
  MeetingResponse createMeeting(CreateMeetingRequest request, Long userId);

  List<MeetingResponse> getAllMeetings();

  List<MeetingResponse> getMyMeetings(Long userId);

  List<MeetingResponse> getMeetingsForGroup(Long groupId);

  void submitAvailability(Long meetingId, AvailabilityRequest request, Long userId);

  List<AvailabilitySummaryResponse> getAvailabilitySummary(Long meetingId);

  MeetingResponse finalizeMeetingTime(Long meetingId, Long userId);

  MeetingResponse getMeetingDetails(Long id);
  
  MeetingResponse updateMeeting(Long id, CreateMeetingRequest request, Long userId);

  void deleteMeeting(Long id, Long userId);
}
