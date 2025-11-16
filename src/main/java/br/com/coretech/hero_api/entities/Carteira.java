package br.com.coretech.hero_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "tb_carteira")
public class Carteira {

    @Id
    private Long id; //  O mesmo ID do Usuário (relação 1-para-1)

    // Define quem é o dono desta carteira
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Pega o ID da relação @OneToOne como PK desta entidade
    @JoinColumn(name = "usuario_id")
    private Usuario menor;

    // O saldo atual de fichas
    @Column(nullable = false)
    private Integer saldoFichas = 0;

    // O saldo atual do "cofre" em R$ [cite: 46]
    @Column(nullable = false)
    private Double saldoDinheiro = 0.0;

    // O "extrato" de fichas
    @OneToMany(mappedBy = "carteira", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransacaoFicha> historicoFichas;

    // O "extrato" do cofre
    @OneToMany(mappedBy = "carteira", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransacaoDinheiro> historicoDinheiro;
}
