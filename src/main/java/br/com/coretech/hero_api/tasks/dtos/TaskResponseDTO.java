package br.com.coretech.hero_api.tasks.dtos;

import br.com.coretech.hero_api.tasks.entities.Task;
import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Integer rawardTask;
    private TaskStatus status;
    private Long minorId;
    private String minorNome;
    private LocalDateTime criatioDate;
    private LocalDateTime completedDate;

    // Metodo estático para converter Entidade em DTO
    public static TaskResponseDTO fromEntity(Task task) {
        if (task == null) return null;

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setRawardTask(task.getTokenRaward());
        dto.setStatus(task.getStatus());

        // Evita NullPointerException caso o menor não venha preenchido
        if (task.getMinor() != null) {
            dto.setMinorId(task.getMinor().getId());
            dto.setMinorNome(task.getMinor().getName());
        }

        dto.setCriatioDate(task.getCreationDate());
        dto.setCompletedDate(task.getCompletedDate());

        return dto;
    }
}