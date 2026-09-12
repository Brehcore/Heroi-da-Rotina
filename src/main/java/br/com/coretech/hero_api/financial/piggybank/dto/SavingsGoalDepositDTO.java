package br.com.coretech.hero_api.financial.piggybank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SavingsGoalDepositDTO(
        @NotNull(message = "O valor do depósito é obrigatório.")
        @DecimalMin(value = "0.50", message = "O depósito mínimo é de R$ 0,50.")
        BigDecimal amount
) {}