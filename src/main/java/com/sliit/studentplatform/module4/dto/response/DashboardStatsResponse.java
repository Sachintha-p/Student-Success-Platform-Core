package com.sliit.studentplatform.module4.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalResources;
    private long totalBookmarks;
    private long totalAiChats;
    private String trendingSubject;
    private Map<String, Long> resourceDistribution; 
    private Map<String, Long> topAiTopics;
    private java.util.List<ConversationSummaryResponse> recentConversations;
}
