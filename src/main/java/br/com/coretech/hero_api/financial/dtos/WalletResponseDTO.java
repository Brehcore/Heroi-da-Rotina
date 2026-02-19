package br.com.coretech.hero_api.financial.dtos;

import lombok.Data;

@Data
public class WalletResponseDTO {
    private Long id;
    private Long menorId;
    private String menorNome;
    private Integer saldoFichas;
    private Double saldoDinheiro;
}