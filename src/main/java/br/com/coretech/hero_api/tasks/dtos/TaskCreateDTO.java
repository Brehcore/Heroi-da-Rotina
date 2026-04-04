package br.com.coretech.hero_api.tasks.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados necessários para a criação de uma nova tarefa")
public class TaskCreateDTO {

    @Schema(description = "Título curto da tarefa", example = "Arrumar o quarto")
    private String title;

    @Schema(description = "Detalhamento do que deve ser feito", example = "Organizar os brinquedos e dobrar as roupas")
    private String description;

    @Schema(description = "Quantidade de fichas/tokens que serão atribuídos como recompensa", example = "10")
    private Integer tokenReward;

    @Schema(description = "ID do usuário (menor/dependente) que deverá realizar a tarefa", example = "5")
    private Long minorId;

    @Schema(description = "ID do monitor/responsável que está criando a tarefa", example = "2")
    private Long monitorCreatorId;
}