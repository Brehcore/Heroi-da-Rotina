package br.com.coretech.hero_api.financial.piggybank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SavingsGoalCreateDTO(
        @NotBlank(message = "O título da meta é obrigatório.")
        String title,

        String description,

        @NotNull(message = "O valor da meta é obrigatório.")
        @DecimalMin(value = "1.00", message = "O valor da meta deve ser de pelo menos R$ 1,00.")
        BigDecimal targetAmount,

        LocalDate targetDate,

        String icon
) {}