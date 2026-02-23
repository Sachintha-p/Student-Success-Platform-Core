package com.sliit.studentplatform.module2.service.interfaces;

import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module2.dto.request.JobListingRequest;
import com.sliit.studentplatform.module2.entity.JobListing;
import org.springframework.data.domain.Pageable;

public interface IJobService {
  JobListing createListing(JobListingRequest request, Long posterId);

  JobListing getListingById(Long id);

  PagedResponse<JobListing> listActiveJobs(Pageable pageable);

  JobListing updateListing(Long id, JobListingRequest request, Long userId);

  void deleteListing(Long id, Long userId);
}
