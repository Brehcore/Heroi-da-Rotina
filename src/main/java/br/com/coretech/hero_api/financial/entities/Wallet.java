package br.com.coretech.hero_api.financial.entities;

import br.com.coretech.hero_api.financial.enums.InterestFrequency;
import br.com.coretech.hero_api.screentime.entities.ScreenTimeConfig;
import br.com.coretech.hero_api.users.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User minor;

    @OneToOne(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private ScreenTimeConfig screenTimeConfig;

    // O saldo atual de fichas
    @Column(nullable = false)
    private Integer tokenBalances = 0;

    // O saldo atual do "cofre" em R$ [cite: 46]
    @Column(nullable = false)
    private Double moneyBalances = 0.0;

    // O "extrato" de fichas
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TokenTransaction> historicalTokens;

    // O "extrato" do cofre
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MoneyTransaction> historicalMoney;

    private Double tokenQuotation;

    private Double interestRate;

    private Boolean interestEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_frequency")
    private InterestFrequency interestFrequency;
}
