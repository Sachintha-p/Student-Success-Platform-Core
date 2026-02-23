package com.sliit.studentplatform.module2.service.interfaces;

import com.sliit.studentplatform.module2.dto.response.CvSuggestionResponse;
import java.util.List;

public interface ICvSuggestionService {
  List<CvSuggestionResponse> generateSuggestions(Long resumeId, Long userId);

  List<CvSuggestionResponse> getSuggestionsForResume(Long resumeId);

  CvSuggestionResponse markApplied(Long suggestionId, Long userId);
}
