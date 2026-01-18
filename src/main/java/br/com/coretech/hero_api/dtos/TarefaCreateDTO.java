package br.com.coretech.hero_api.dtos;

import lombok.Data;

@Data
public class TarefaCreateDTO {
    private String titulo;
    private String descricao;
    private Integer recompensaFichas;
    private Long menorId; // Quem vai realizar a tarefa
    private Long monitorCriadorId; // Quem criou
}