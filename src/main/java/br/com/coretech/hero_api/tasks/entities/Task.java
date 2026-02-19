package br.com.coretech.hero_api.tasks.entities;

import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import br.com.coretech.hero_api.users.entities.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo; // Ex: "Leitura diária" [cite: 24] ou "Arrumar o quarto" [cite: 15]

    private String descricao; // Ex: "30 minutos por dia" [cite: 23]

    // Quantas fichas esta tarefa paga como bônus [cite: 51, 52]
    @Column(nullable = false)
    private Integer recompensaFichas = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDENTE;

    // Quem deve fazer a tarefa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menor_id", nullable = false)
    private User menor;

    // Opcional: quem criou a tarefa (útil no app dos monitores)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_criador_id")
    private User monitorCriador;

    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataConclusao; // Data que o menor marcou como concluída
    private LocalDateTime dataAprovacao; // Data que o monitor aprovou
}
