package br.com.coretech.hero_api.screentime.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ScreenTimeRequestDTO(
        @NotNull(message = "O ID do menor é obrigatório")
        Long minorId,

        @NotNull(message = "A quantidade de minutos é obrigatória")
        @Min(value = 1, message = "O tempo mínimo é de 1 minuto")
        @Max(value = 480, message = "O limite máximo por solicitação é de 8 horas")
        Integer minutes
) {}