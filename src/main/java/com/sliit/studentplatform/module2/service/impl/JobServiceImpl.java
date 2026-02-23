package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.common.exception.UnauthorizedException;
import com.sliit.studentplatform.common.response.PagedResponse;
import com.sliit.studentplatform.module2.dto.request.JobListingRequest;
import com.sliit.studentplatform.module2.entity.JobListing;
import com.sliit.studentplatform.module2.repository.JobListingRepository;
import com.sliit.studentplatform.module2.service.interfaces.IJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements IJobService {

  private final JobListingRepository jobListingRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public JobListing createListing(JobListingRequest request, Long posterId) {
    log.info("Creating job listing '{}' by user: {}", request.getTitle(), posterId);
    User poster = userRepository.findById(posterId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", posterId));

    return jobListingRepository.save(JobListing.builder()
        .title(request.getTitle()).company(request.getCompany())
        .description(request.getDescription()).requiredSkills(request.getRequiredSkills())
        .type(request.getType()).location(request.getLocation())
        .remote(request.isRemote()).deadline(request.getDeadline())
        .postedBy(poster).active(true).build());
  }

  @Override
  @Transactional(readOnly = true)
  public JobListing getListingById(Long id) {
    return jobListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("JobListing", "id", id));
  }

  @Override
  @Transactional(readOnly = true)
  public PagedResponse<JobListing> listActiveJobs(Pageable pageable) {
    return PagedResponse.of(jobListingRepository.findByActiveTrue(pageable));
  }

  @Override
  @Transactional
  public JobListing updateListing(Long id, JobListingRequest request, Long userId) {
    log.info("Updating job listing id: {}", id);
    JobListing listing = getListingById(id);
    if (!listing.getPostedBy().getId().equals(userId)) {
      throw new UnauthorizedException("Only the poster can update this listing");
    }
    listing.setTitle(request.getTitle());
    listing.setCompany(request.getCompany());
    listing.setDescription(request.getDescription());
    listing.setRequiredSkills(request.getRequiredSkills());
    listing.setType(request.getType());
    listing.setLocation(request.getLocation());
    listing.setRemote(request.isRemote());
    listing.setDeadline(request.getDeadline());
    return jobListingRepository.save(listing);
  }

  @Override
  @Transactional
  public void deleteListing(Long id, Long userId) {
    JobListing listing = getListingById(id);
    if (!listing.getPostedBy().getId().equals(userId))
      throw new UnauthorizedException("Only the poster can delete this listing");
    listing.setActive(false);
    jobListingRepository.save(listing); // Soft delete
  }
}
