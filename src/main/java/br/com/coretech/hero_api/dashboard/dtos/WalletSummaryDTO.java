package br.com.coretech.hero_api.dashboard.dtos;

public record WalletSummaryDTO(
        Integer tokenBalances,
        Double moneyBalances,
        Double tokenQuotation,
        Boolean interestEnabled,
        Double interestRate,
        String interestFrequency
) {}
