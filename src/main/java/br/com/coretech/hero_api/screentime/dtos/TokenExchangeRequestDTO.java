package br.com.coretech.hero_api.screentime.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TokenExchangeRequestDTO(
        @NotNull(message = "O ID do menor é obrigatório") Long minorId,
        @NotNull(message = "A quantidade de fichas é obrigatória") @Positive Integer tokens
) {}
