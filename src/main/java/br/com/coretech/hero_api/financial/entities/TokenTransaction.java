package br.com.coretech.hero_api.financial.entities;

import br.com.coretech.hero_api.financial.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tb_token_transaction")
public class TokenTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // CREDITO ou DEBITO

    @Column(nullable = false)
    private Integer value; // Ex: 14 (para "Distribuição Semanal") [cite: 38]

    @Column(nullable = false)
    private String motive; // Ex: "Distribuição Semanal", "Leitura 30min" [cite: 51], "Multa" [cite: 55]

    private LocalDateTime date = LocalDateTime.now();
}
