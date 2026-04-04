package br.com.coretech.hero_api.financial.dtos;

import br.com.coretech.hero_api.financial.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Representa uma movimentação financeira ou de fichas no extrato")
public class TransactionDTO {

    @Schema(description = "Identificador único da transação", example = "42")
    private Long id;

    @Schema(description = "Tipo da movimentação", example = "CREDITO")
    private TransactionType type;

    @Schema(description = "Motivo ou descrição da transação", example = "Compra de Lego")
    private String motive;

    @Schema(description = "Valor formatado com unidade de medida", example = "15 Fichas")
    private String formattedValue;

    @Schema(description = "Data e hora em que a transação ocorreu", example = "2026-04-03T15:30:00")
    private LocalDateTime date;
}