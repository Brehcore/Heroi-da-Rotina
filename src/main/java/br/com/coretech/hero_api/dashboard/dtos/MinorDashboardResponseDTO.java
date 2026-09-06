package br.com.coretech.hero_api.dashboard.dtos;

import java.util.List;

public record MinorDashboardResponseDTO(
        WalletSummaryDTO wallet,
        ScreenTimeSummaryDTO screenTime,
        List<TaskSummaryDTO> todayTasks,
        GamificationSummaryDTO gamification,
        String dailyReminder
) {}
