package br.com.coretech.hero_api.dtos;

import lombok.Data;

@Data
public class CarteiraResponseDTO {
    private Long id;
    private Long menorId;
    private String menorNome;
    private Integer saldoFichas;
    private Double saldoDinheiro;
}