package br.com.coretech.hero_api.financial.transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ConvertTokensDTO(
        @NotNull(message = "A quantidade de fichas é obrigatória.")
        @Positive(message = "A quantidade de fichas para conversão deve ser maior que zero.")
        Integer tokensToConvert
) {}