package br.com.coretech.hero_api.dtos;

import br.com.coretech.hero_api.enums.StatusTarefa;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TarefaResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private Integer recompensaFichas;
    private StatusTarefa status;
    private Long menorId;
    private String menorNome; // Para o Monitor saber de quem é a tarefa na lista geral
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;
}