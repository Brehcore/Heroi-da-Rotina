package br.com.coretech.hero_api.financial.wallet.service;

import br.com.coretech.hero_api.financial.wallet.dto.WalletResponseDTO;
import br.com.coretech.hero_api.financial.wallet.entity.MoneyTransaction;
import br.com.coretech.hero_api.financial.wallet.entity.Wallet;
import br.com.coretech.hero_api.financial.wallet.enums.InterestFrequency;
import br.com.coretech.hero_api.financial.transaction.enums.TransactionType;
import br.com.coretech.hero_api.financial.wallet.repository.WalletRepository;
import br.com.coretech.hero_api.mappers.HeroMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final HeroMapper heroMapper;

    /**
     * Busca os detalhes da carteira de um menor e converte para DTO.
     * É aqui que o HeroMapper passa a ter uso nesta classe.
     */
    @Transactional(readOnly = true)
    public WalletResponseDTO getWalletByMinorId(Long minorId) {
        return walletRepository.findByMinorId(minorId)
                .map(heroMapper::toWalletDTO) //
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada para o menor ID: " + minorId));
    }

    /**
     * Atualiza o valor de cotação de cada ficha.
     */
    @Transactional
    public void updateQuotation(Long minorId, Double newQuotation) {
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Wallet não encontrada"));
        wallet.setTokenQuotation(newQuotation);
        walletRepository.save(wallet);
    }

    /**
     * Liga/Desliga os juros, define a taxa e a frequência (Diário, Semanal, Mensal)
     */
    @Transactional
    public void updateInterestConfig(Long minorId, Double rate, Boolean enabled, InterestFrequency frequency) {
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        wallet.setInterestRate(rate);
        wallet.setInterestEnabled(enabled);
        wallet.setInterestFrequency(frequency); // <-- Salvando a nova escolha
        walletRepository.save(wallet);
    }

    // ========================================================================
    // ROTINAS DE RENDIMENTO AUTOMÁTICO (CRON JOBS)
    // ========================================================================

    /**
     * Roda TODOS OS DIAS à meia-noite (00:00).
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void applyDailyInterest() {
        List<Wallet> walletsToProcess = walletRepository.findAllByInterestEnabledTrueAndInterestFrequency(InterestFrequency.DAILY);
        processInterestForWallets(walletsToProcess, "Diário");
    }

    /**
     * Roda toda SEGUNDA-FEIRA à meia-noite (00:00).
     */
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void applyWeeklyInterest() {
        List<Wallet> walletsToProcess = walletRepository.findAllByInterestEnabledTrueAndInterestFrequency(InterestFrequency.WEEKLY);
        processInterestForWallets(walletsToProcess, "Semanal");
    }

    /**
     * Roda todos os DIA 1º DO MÊS à meia-noite (00:00).
     */
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void applyMonthlyInterest() {
        List<Wallet> walletsToProcess = walletRepository.findAllByInterestEnabledTrueAndInterestFrequency(InterestFrequency.MONTHLY);
        processInterestForWallets(walletsToProcess, "Mensal");
    }

    /**
     * Metodo auxiliar privado para não repetir a lógica matemática e de histórico.
     */
    private void processInterestForWallets(List<Wallet> wallets, String frequencyLabel) {
        if (wallets.isEmpty()) return;

        for (Wallet wallet : wallets) {
            double balance = wallet.getMoneyBalances();
            double rate = wallet.getInterestRate() != null ? wallet.getInterestRate() : 0.0;
            double interestValue = balance * (rate / 100);

            if (interestValue > 0) {
                wallet.setMoneyBalances(balance + interestValue);

                MoneyTransaction tx = new MoneyTransaction();
                tx.setWallet(wallet);
                tx.setType(TransactionType.CREDIT);
                tx.setValue(interestValue);
                tx.setMotive(String.format("Rendimento %s aplicado: %.2f%%", frequencyLabel, rate));
                tx.setDate(LocalDateTime.now());

                wallet.getHistoricalMoney().add(tx);
            }
        }

        walletRepository.saveAll(wallets);
    }

}