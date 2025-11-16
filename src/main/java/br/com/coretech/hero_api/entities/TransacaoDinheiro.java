package br.com.coretech.hero_api.entities;

import br.com.coretech.hero_api.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_transacao_dinheiro")
public class TransacaoDinheiro {

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
    private Double valor; // Ex: 0.70 [cite: 46]

    @Column(nullable = false)
    private String motivo; // Ex: "Conversão de 1 ficha" [cite: 46], "Juros Semanais" [cite: 49]

    private LocalDateTime data = LocalDateTime.now();
}
