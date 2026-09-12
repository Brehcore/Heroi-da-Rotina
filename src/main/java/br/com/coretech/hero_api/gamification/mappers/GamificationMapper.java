package br.com.coretech.hero_api.gamification.mappers;

import br.com.coretech.hero_api.gamification.dtos.GamificationResponseDTO;
import br.com.coretech.hero_api.gamification.entities.UserGamification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GamificationMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "progressPercentage", source = "gamification", qualifiedByName = "calculateProgress")
    GamificationResponseDTO toDTO(UserGamification gamification);

    @Named("calculateProgress")
    default Double calculateProgress(UserGamification gamification) {
        if (gamification == null || gamification.getTargetXp() == null || gamification.getTargetXp() <= 0) {
            return 0.0;
        }

        int currentXp = gamification.getCurrentXp() != null ? gamification.getCurrentXp() : 0;
        double progress = ((double) currentXp / gamification.getTargetXp()) * 100.0;
        double rounded = Math.round(progress * 100.0) / 100.0;

        return Math.clamp(rounded, 0.0, 100.0);
    }
}