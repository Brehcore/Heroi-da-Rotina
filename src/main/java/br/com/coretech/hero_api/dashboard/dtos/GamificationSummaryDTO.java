package br.com.coretech.hero_api.dashboard.dtos;

public record GamificationSummaryDTO(
        Integer currentLevel,
        Integer currentXp,
        Integer targetXp,
        Integer completedTasksToday,
        Integer totalTasksToday,
        String motivationalMessage
) {}