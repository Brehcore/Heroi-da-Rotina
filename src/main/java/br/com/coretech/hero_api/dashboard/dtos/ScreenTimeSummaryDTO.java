package br.com.coretech.hero_api.dashboard.dtos;

public record ScreenTimeSummaryDTO(
        Integer dailyLimitMinutes,
        Integer usedMinutesToday,
        Integer remainingMinutesToday,
        Integer tokenToMinutesRatio // Ex: 1 ficha = 30 min
) {}
