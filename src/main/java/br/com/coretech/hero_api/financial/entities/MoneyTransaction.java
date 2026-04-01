package br.com.coretech.hero_api.financial.entities;

import br.com.coretech.hero_api.financial.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_money_transaction")
public class MoneyTransaction {

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
    private Double value; // Ex: 0.70 [cite: 46]

    @Column(nullable = false)
    private String motive; // Ex: "Conversão de 1 ficha" [cite: 46], "Juros Semanais" [cite: 49]

    private LocalDateTime date = LocalDateTime.now();
}
