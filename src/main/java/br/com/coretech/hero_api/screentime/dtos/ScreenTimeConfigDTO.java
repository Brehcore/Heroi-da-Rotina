package br.com.coretech.hero_api.screentime.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Objeto de transferência para as configurações de tempo de tela e limites semanais")
public class ScreenTimeConfigDTO {

    @Schema(description = "Quantidade de minutos que cada ficha/token concede", example = "30")
    private Integer minutesPerToken;

    @Schema(description = "Limite máximo de minutos permitidos na segunda-feira", example = "120")
    private Integer mondayLimit;

    @Schema(description = "Limite máximo de minutos permitidos na terça-feira", example = "120")
    private Integer tuesdayLimit;

    @Schema(description = "Limite máximo de minutos permitidos na quarta-feira", example = "120")
    private Integer wednesdayLimit;

    @Schema(description = "Limite máximo de minutos permitidos na quinta-feira", example = "120")
    private Integer thursdayLimit;

    @Schema(description = "Limite máximo de minutos permitidos na sexta-feira", example = "120")
    private Integer fridayLimit;

    @Schema(description = "Limite máximo de minutos permitidos no sábado", example = "240")
    private Integer saturdayLimit;

    @Schema(description = "Limite máximo de minutos permitidos no domingo", example = "240")
    private Integer sundayLimit;
}