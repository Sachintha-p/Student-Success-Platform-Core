package com.sliit.studentplatform.auth.service.interfaces;

import com.sliit.studentplatform.auth.dto.request.UpdateStudentProfileRequest;
import com.sliit.studentplatform.auth.dto.response.UserProfileResponse;

public interface IStudentService {

    UserProfileResponse getCurrentProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UpdateStudentProfileRequest request);
}