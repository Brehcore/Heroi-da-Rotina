package br.com.coretech.hero_api.financial.piggybank.mappers;

import br.com.coretech.hero_api.financial.piggybank.dto.SavingsGoalResponseDTO;
import br.com.coretech.hero_api.financial.piggybank.entity.SavingsGoal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsGoalMapper {

    @Mapping(target = "progressPercentage", source = "goal", qualifiedByName = "calculateProgress")
    SavingsGoalResponseDTO toDTO(SavingsGoal goal);

    List<SavingsGoalResponseDTO> toDTOList(List<SavingsGoal> goals);

    @Named("calculateProgress")
    default Double calculateProgress(SavingsGoal goal) {
        if (goal.getTargetAmount() == null || goal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        BigDecimal current = goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO;
        double progress = current.divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
        return Math.clamp(progress, 0.0, 100.0);
    }
}