package com.sliit.studentplatform.module3.service.interfaces;

import com.sliit.studentplatform.module3.dto.request.RSVPRequest;
import com.sliit.studentplatform.module3.dto.response.EventResponse;
import com.sliit.studentplatform.module3.dto.response.RSVPResponse;

import java.util.List;

public interface IRSVPService {
    RSVPResponse rsvpToEvent(Long eventId, Long studentId, RSVPRequest request);

    RSVPResponse updateRSVPStatus(Long eventId, Long studentId, RSVPRequest request);

    List<RSVPResponse> getAllRSVPsForEvent(Long eventId);

    List<EventResponse> getEventsJoinedByStudent(Long studentId);
}
