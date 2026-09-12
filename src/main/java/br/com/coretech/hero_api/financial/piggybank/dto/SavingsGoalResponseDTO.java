package br.com.coretech.hero_api.financial.piggybank.dto;

import br.com.coretech.hero_api.financial.piggybank.enums.SavingsGoalStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SavingsGoalResponseDTO(
        Long id,
        String title,
        String description,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        Double progressPercentage,
        LocalDate targetDate,
        String icon,
        SavingsGoalStatus status
) {}