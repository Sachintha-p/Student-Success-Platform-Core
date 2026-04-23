package com.sliit.studentplatform.module2.service.interfaces;

import com.sliit.studentplatform.module2.dto.request.JobListingRequest;
import com.sliit.studentplatform.module2.dto.response.JobListingResponse;
import java.util.List;

public interface IJobListingService {

    // Now accepts the request and the ID of the admin creating the job
    JobListingResponse createJob(JobListingRequest request, Long userId);

    JobListingResponse updateJob(Long id, JobListingRequest request);

    void deleteJob(Long id);

    List<JobListingResponse> getAllJobs();

    JobListingResponse getJobById(Long id);
}