package br.com.coretech.hero_api.screentime.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScreenTimeResponseDTO(

        Long requestId,

        @Schema(description = "Id de quem pediu o tempo de tela", example = "3")
        Long minorId,

        @Schema(description = "Nome de quem pediu o tempo de tela", example = "Maria Silva")
        String minorName,

        String status,

        @Schema(description = "Quantidade de minutos solicitados", example = "15")
        Integer requestedMinutes,

        @Schema(description = "Quantidade de minutos restantes", example = "10")
        Integer remainingBalance
) {}