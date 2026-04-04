package br.com.coretech.hero_api.tasks.dtos;

import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Dados de retorno detalhados de uma tarefa")
public class TaskResponseDTO {

    @Schema(description = "Identificador único da tarefa", example = "101")
    private Long id;

    @Schema(description = "Título da tarefa", example = "Arrumar o quarto")
    private String title;

    @Schema(description = "Descrição detalhada da tarefa", example = "Organizar os brinquedos e dobrar as roupas")
    private String description;

    @Schema(description = "Valor da recompensa em tokens ao concluir", example = "10")
    private Integer rewardTask;

    @Schema(description = "Status atual da tarefa (PENDENTE, CONCLUIDA, etc.)", example = "PENDENTE")
    private TaskStatus status;

    @Schema(description = "ID do executor da tarefa", example = "5")
    private Long minorId;

    @Schema(description = "Nome do executor da tarefa", example = "Miles Morales")
    private String minorName;

    @Schema(description = "Data e hora de criação do registro", example = "2026-04-03T20:51:00")
    private LocalDateTime creationDate;

    @Schema(description = "Data e hora em que a tarefa foi marcada como concluída", example = "null")
    private LocalDateTime completedDate;
}