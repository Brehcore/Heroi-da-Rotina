package br.com.coretech.hero_api.tasks.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados para reprovar uma tarefa e solicitar que seja refeita")
public class TaskRejectDTO {

    @Schema(description = "Motivo pelo qual a tarefa foi reprovada", example = "Você guardou os brinquedos, mas esqueceu de arrumar a cama.")
    private String reason;
}