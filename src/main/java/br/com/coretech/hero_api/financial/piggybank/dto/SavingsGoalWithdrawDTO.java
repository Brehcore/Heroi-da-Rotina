package br.com.coretech.hero_api.financial.piggybank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SavingsGoalWithdrawDTO(
        @NotNull(message = "O valor de resgate é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor mínimo de resgate é de R$ 0,01.")
        BigDecimal amount
) {}