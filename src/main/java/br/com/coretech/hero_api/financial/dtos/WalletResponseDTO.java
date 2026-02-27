package br.com.coretech.hero_api.financial.dtos;

import lombok.Data;

@Data
public class WalletResponseDTO {
    private Long id;
    private Long minorId;
    private String minorName;
    private Integer TokensBalance;
    private Double MoneyBalance;
}