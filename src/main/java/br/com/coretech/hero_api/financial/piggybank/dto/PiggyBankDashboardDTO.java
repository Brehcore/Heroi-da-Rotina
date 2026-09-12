package br.com.coretech.hero_api.financial.piggybank.dto;

import java.math.BigDecimal;
import java.util.List;

public record PiggyBankDashboardDTO(
        BigDecimal liquidWalletBalance,    // Saldo livre na carteira (tb_wallets.money_balances)
        BigDecimal totalSavedInGoals,       // Total guardado em todas as metas
        BigDecimal totalPiggyBalance,       // Soma do saldo livre + total nas metas
        boolean interestEnabled,
        Double interestRate,
        String interestFrequency,
        BigDecimal projectedYieldNextCycle, // Rendimento estimado no próximo ciclo
        String yieldMotivationMessage,
        List<SavingsGoalResponseDTO> goals
) {}