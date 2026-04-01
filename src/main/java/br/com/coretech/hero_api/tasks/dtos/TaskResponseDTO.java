package br.com.coretech.hero_api.tasks.dtos;

import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Integer rewardTask;
    private TaskStatus status;
    private Long minorId;
    private String minorName;
    private LocalDateTime creationDate;
    private LocalDateTime completedDate;

}