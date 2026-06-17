package br.com.coretech.hero_api.financial.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DeductTokensRequestDTO {

    @NotNull(message = "A quantidade de fichas é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    Integer amount;

    @NotBlank(message = "O motivo é obrigatório para auditoria")
    String motive;
}
