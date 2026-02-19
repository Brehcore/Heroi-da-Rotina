package br.com.coretech.hero_api.tasks.dtos;

import br.com.coretech.hero_api.tasks.entities.Task;
import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private Integer recompensaFichas;
    private TaskStatus status;
    private Long menorId;
    private String menorNome;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;

    // Metodo estático para converter Entidade em DTO
    public static TaskResponseDTO fromEntity(Task task) {
        if (task == null) return null;

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitulo(task.getTitulo());
        dto.setDescricao(task.getDescricao());
        dto.setRecompensaFichas(task.getRecompensaFichas());
        dto.setStatus(task.getStatus());

        // Evita NullPointerException caso o menor não venha preenchido
        if (task.getMenor() != null) {
            dto.setMenorId(task.getMenor().getId());
            dto.setMenorNome(task.getMenor().getNome());
        }

        dto.setDataCriacao(task.getDataCriacao());
        dto.setDataConclusao(task.getDataConclusao());

        return dto;
    }
}