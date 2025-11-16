package br.com.coretech.hero_api.entities;

import br.com.coretech.hero_api.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_transacao_ficha")
public class TransacaoFicha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", nullable = false)
    private Carteira carteira;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo; // CREDITO ou DEBITO

    @Column(nullable = false)
    private Integer valor; // Ex: 14 (para "Distribuição Semanal") [cite: 38]

    @Column(nullable = false)
    private String motivo; // Ex: "Distribuição Semanal", "Leitura 30min" [cite: 51], "Multa" [cite: 55]

    private LocalDateTime data = LocalDateTime.now();
}
