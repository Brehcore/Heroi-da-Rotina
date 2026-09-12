package br.com.coretech.hero_api.financial.wallet.dto;

import br.com.coretech.hero_api.financial.wallet.enums.InterestFrequency;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa a configuração da cotação do token/ficha")
public record InterestConfigDTO (
        @Schema(description = "Taxa de juros aplicada", example = "2%")
        Double rate,

        @Schema(description = "Ativa/Inativa a taxa de juros")
        Boolean enabled,

        @Schema(description = "Define a frequência da taxa aplicada", example = "Diariamente, Semanalmente e Mensalmente")
        InterestFrequency frequency
){}