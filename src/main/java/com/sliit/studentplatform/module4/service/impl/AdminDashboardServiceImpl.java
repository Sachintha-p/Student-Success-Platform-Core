package com.sliit.studentplatform.module4.service.impl;

import com.sliit.studentplatform.module4.dto.response.ConversationSummaryResponse;
import com.sliit.studentplatform.module4.dto.response.DashboardStatsResponse;
import com.sliit.studentplatform.module4.repository.BookmarkRepository;
import com.sliit.studentplatform.module4.repository.ChatMessageRepository;
import com.sliit.studentplatform.module4.repository.ConversationRepository;
import com.sliit.studentplatform.module4.repository.StudyResourceRepository;
import com.sliit.studentplatform.module4.service.interfaces.IAdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements IAdminDashboardService {

    private final StudyResourceRepository resourceRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalResources = resourceRepository.count();
        long totalBookmarks = bookmarkRepository.count();
        long totalAiChats = conversationRepository.count();

        // Trending Subject
        List<Object[]> subjectCounts = resourceRepository.countResourcesBySubject();
        String trendingSubject = (subjectCounts != null && !subjectCounts.isEmpty()) 
                                 ? (String) subjectCounts.get(0)[0] 
                                 : "None";

        // Resource Type Distribution
        Map<String, Long> resourceDistribution = new HashMap<>();
        List<Object[]> typeCounts = resourceRepository.countResourcesByType();
        if (typeCounts != null) {
            for (Object[] row : typeCounts) {
                resourceDistribution.put((String) row[0], (Long) row[1]);
            }
        }

        // Top AI Assistant Topics
        Map<String, Long> topAiTopics = new HashMap<>();
        List<Object[]> aiTopicCounts = conversationRepository.countConversationsBySubject();
        if (aiTopicCounts != null) {
            for (Object[] row : aiTopicCounts) {
                String subjectLabel = (row[0] != null) ? (String) row[0] : "General Query";
                topAiTopics.put(subjectLabel, (Long) row[1]);
            }
        }

        // Recent Conversations for Moderation
        List<ConversationSummaryResponse> recentConversations = conversationRepository.findTop10ByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(conv -> ConversationSummaryResponse.builder()
                        .id(conv.getId())
                        .userId(conv.getUser().getId())
                        .studentName(conv.getUser().getFullName())
                        .title(conv.getTitle())
                        .subject(conv.getSubject())
                        .createdAt(conv.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return DashboardStatsResponse.builder()
                .totalResources(totalResources)
                .totalBookmarks(totalBookmarks)
                .totalAiChats(totalAiChats)
                .trendingSubject(trendingSubject)
                .resourceDistribution(resourceDistribution)
                .topAiTopics(topAiTopics)
                .recentConversations(recentConversations)
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteConversation(Long id) {
        chatMessageRepository.deleteByConversationId(id);
        conversationRepository.deleteById(id);
    }
}
