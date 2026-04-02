package com.sliit.studentplatform.module2.service.interfaces;

import com.sliit.studentplatform.module2.dto.request.JobListingRequest;
import com.sliit.studentplatform.module2.dto.response.JobListingResponse;
import java.util.List;

public interface IJobListingService {

    JobListingResponse createJob(JobListingRequest request);

    JobListingResponse updateJob(Long id, JobListingRequest request);

    void deleteJob(Long id);

    List<JobListingResponse> getAllJobs();

    // >>> ADD THIS METHOD SO THE SCANNER CAN FIND THE JOB <<<
    JobListingResponse getJobById(Long id);
}