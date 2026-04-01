package br.com.coretech.hero_api.tasks.entities;

import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import br.com.coretech.hero_api.users.entities.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tb_tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Ex: "Leitura diária" [cite: 24] ou "Arrumar o quarto" [cite: 15]

    private String description; // Ex: "30 minutos por dia" [cite: 23]

    // Quantas fichas esta tarefa paga como bônus [cite: 51, 52]
    @Column(nullable = false)
    private Integer tokenReward = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    // Quem deve fazer a tarefa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minor_id", nullable = false)
    private User minor;

    // Opcional: quem criou a tarefa (útil no app dos monitores)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_creater_id")
    private User monitorCreator;

    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime CompletedDate; // Data que o menor marcou como concluída
    private LocalDateTime ApprovalDate; // Data que o monitor aprovou
}
