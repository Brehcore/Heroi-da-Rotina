package br.com.coretech.hero_api.tasks.dtos;

import lombok.Data;

@Data
public class TaskCreateDTO {
    private String title;
    private String description;
    private Integer tokenReward;
    private Long minorId; // Quem vai realizar a tarefa
    private Long monitorCreatorId; // Quem criou
}