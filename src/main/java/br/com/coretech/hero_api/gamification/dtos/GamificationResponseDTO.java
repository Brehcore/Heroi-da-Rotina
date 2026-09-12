package br.com.coretech.hero_api.gamification.dtos;

public record GamificationResponseDTO(
        Long userId,
        Integer currentLevel,
        Integer currentXp,
        Integer targetXp,
        Double progressPercentage
) {
}
