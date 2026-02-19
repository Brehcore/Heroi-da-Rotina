package br.com.coretech.hero_api.financial.entities;

import br.com.coretech.hero_api.financial.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_token_transaction")
public class TokenTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType tipo; // CREDITO ou DEBITO

    @Column(nullable = false)
    private Integer valor; // Ex: 14 (para "Distribuição Semanal") [cite: 38]

    @Column(nullable = false)
    private String motivo; // Ex: "Distribuição Semanal", "Leitura 30min" [cite: 51], "Multa" [cite: 55]

    private LocalDateTime data = LocalDateTime.now();
}
