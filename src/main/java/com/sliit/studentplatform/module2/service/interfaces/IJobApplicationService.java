package com.sliit.studentplatform.module2.service.interfaces;

import com.sliit.studentplatform.module2.dto.request.JobApplicationRequest;
import com.sliit.studentplatform.module2.dto.response.JobApplicationResponse;
import com.sliit.studentplatform.common.enums.Status;

import java.util.List;

public interface IJobApplicationService {
    JobApplicationResponse apply(JobApplicationRequest request, Long userId);
    List<JobApplicationResponse> getMyApplications(Long userId);
    List<JobApplicationResponse> getApplicationsForJob(Long jobListingId);

    // NEW ADMIN METHODS
    List<JobApplicationResponse> getAllApplications();
    void deleteApplicationAdmin(Long applicationId);

    JobApplicationResponse updateApplicationStatus(Long applicationId, Status status, String notes);
    void withdrawApplication(Long applicationId, Long userId);
}