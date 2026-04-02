package com.sliit.studentplatform.module4.service.interfaces;

import com.sliit.studentplatform.module4.dto.response.DashboardStatsResponse;

public interface IAdminDashboardService {
    DashboardStatsResponse getDashboardStats();
    void deleteConversation(Long id);
}
