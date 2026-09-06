package br.com.coretech.hero_api.dashboard.dtos;

public record TaskSummaryDTO(
        Long id,
        String title,
        String description,
        Integer tokenReward,
        String status
        // String periodicity // Pendente para próximas features na entidade
) {}