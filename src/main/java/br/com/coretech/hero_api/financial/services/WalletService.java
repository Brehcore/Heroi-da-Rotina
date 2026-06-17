package br.com.coretech.hero_api.financial.services;

import br.com.coretech.hero_api.exceptions.InsufficientBalanceException;
import br.com.coretech.hero_api.financial.dtos.TransactionDTO;
import br.com.coretech.hero_api.financial.dtos.WalletResponseDTO;
import br.com.coretech.hero_api.financial.entities.MoneyTransaction;
import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.entities.TokenTransaction;
import br.com.coretech.hero_api.financial.enums.InterestFrequency;
import br.com.coretech.hero_api.financial.enums.TransactionType;
import br.com.coretech.hero_api.financial.repositories.TokenTransactionRepository;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.utils.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TokenTransactionRepository  tokenTransactionRepository;
    private final HeroMapper heroMapper;
    private final EmailNotificationService emailService;

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

    @Transactional(readOnly = true)
    public Page<TransactionDTO> getMinorTransactionalHistory(Long minorId, Pageable pageable) {
        Page<TokenTransaction> transactions = tokenTransactionRepository.findByWalletMinorId(minorId, pageable);
        return transactions.map(heroMapper::toTokenTransactionDTO);
    }

    /**
     * Adiciona fichas à carteira do menor e registra o histórico.
     */
    @Transactional
    public void tokenDeposit(Long minorId, Integer amount, String motive) {
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Wallet não encontrada para o menor ID: " + minorId));

        // 1. Atualiza saldo
        wallet.setTokenBalances(wallet.getTokenBalances() + amount);

        // 2. Cria registro de transação
        TokenTransaction transaction = new TokenTransaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.CREDIT);
        transaction.setValue(amount);
        transaction.setMotive(motive);
        transaction.setDate(LocalDateTime.now());

        // 3. Adiciona à lista
        wallet.getHistoricalTokens().add(transaction);

        // 4. Salva tudo
        walletRepository.save(wallet);

        User menor = wallet.getMinor();
        if (menor != null && menor.getEmail() != null) {
            String assunto = "🪙 Depósito de Fichas: + " + amount + " no cofre!";
            String corpoHtml = String.format("""
                <div style="font-family: Arial, sans-serif; background-color: #f4f7f6; padding: 20px;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
                        <div style="background-color: #F39C12; padding: 20px; text-align: center; color: white;">
                            <h2 style="margin: 0;">💰 Você Recebeu um Depósito!</h2>
                        </div>
                        <div style="padding: 30px; color: #333333; line-height: 1.6;">
                            <p style="font-size: 18px;">Oi, <strong>%s</strong>!</p>
                            <p>O seu cofre acabou de ficar mais cheio. Você recebeu novas fichas!</p>
                            <div style="background-color: #fff9e6; border: 1px solid #fce3a1; border-radius: 8px; padding: 20px; margin: 25px 0; text-align: center;">
                                <h3 style="margin: 0; color: #d68910; font-size: 28px;">🪙 +%d Fichas</h3>
                                <p style="margin: 15px 0 0 0; font-size: 15px; color: #555;"><strong>Motivo:</strong> %s</p>
                            </div>
                            <p style="text-align: center;">Abra o aplicativo para conferir o seu saldo atualizado.</p>
                        </div>
                    </div>
                </div>
                """, menor.getName(), amount, motive);

            emailService.sendEmail(menor.getEmail(), assunto, corpoHtml);
        }
    }

    @Transactional
    public void tokenDeduct (Long minorId, Integer amount, String motive) {
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Wallet não encontrada para o menor ID: " + minorId));
        if (wallet.getTokenBalances() < amount) {
            throw new InsufficientBalanceException("Fichas insuficientes para essa dedução");
        }

        // 1. Atualiza saldo
        wallet.setTokenBalances(wallet.getTokenBalances() - amount);

        // 2. Cria registro de transação
        TokenTransaction transaction = new TokenTransaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.DEBIT);
        transaction.setValue(amount);
        transaction.setMotive(motive);
        transaction.setDate(LocalDateTime.now());

        // 3. Adiciona à lista
        wallet.getHistoricalTokens().add(transaction);

        // 4. Salva tudo
        walletRepository.save(wallet);
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
     * Converte todas as fichas atuais em dinheiro com base na cotação salva.
     */
    @Transactional
    public void convertTokensToMoney(Long minorId) {
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Wallet não encontrada"));

        int tokensParaConverter = wallet.getTokenBalances();
        double cotacaoAtual = wallet.getTokenQuotation() != null ? wallet.getTokenQuotation() : 0.0;

        if (tokensParaConverter <= 0) {
            throw new RuntimeException("Não há fichas suficientes para conversão.");
        }

        double valorConvertido = tokensParaConverter * cotacaoAtual;

        // 1. Zera as fichas e adiciona o dinheiro
        wallet.setTokenBalances(0);
        wallet.setMoneyBalances(wallet.getMoneyBalances() + valorConvertido);

        // 2. Registrar histórico de Débito de Fichas
        TokenTransaction tokenTx = new TokenTransaction();
        tokenTx.setWallet(wallet);
        tokenTx.setType(TransactionType.DEBIT);
        tokenTx.setValue(tokensParaConverter);
        tokenTx.setMotive("Conversão de fichas em dinheiro");
        tokenTx.setDate(LocalDateTime.now());
        wallet.getHistoricalTokens().add(tokenTx);

        // 3. Registrar histórico de Crédito de Dinheiro
        MoneyTransaction moneyTx = new MoneyTransaction();
        moneyTx.setWallet(wallet);
        moneyTx.setType(TransactionType.CREDIT);
        moneyTx.setValue(valorConvertido);
        moneyTx.setMotive("Recebido da conversão de " + tokensParaConverter + " fichas");
        moneyTx.setDate(LocalDateTime.now());
        wallet.getHistoricalMoney().add(moneyTx);

        walletRepository.save(wallet);
    }

    /**
     * Remove fichas da carteira (punição ou uso de tela direto).
     */
    @Transactional
    public void withdrawTokens(Long minorId, Integer amount, String motive) {
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Wallet não encontrada"));

        if (wallet.getTokenBalances() < amount) {
            throw new RuntimeException("Saldo de fichas insuficiente!");
        }

        wallet.setTokenBalances(wallet.getTokenBalances() - amount);

        TokenTransaction transaction = new TokenTransaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.DEBIT);
        transaction.setValue(amount);
        transaction.setMotive(motive);
        transaction.setDate(LocalDateTime.now());

        wallet.getHistoricalTokens().add(transaction);
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
        System.out.println("Rendimento " + frequencyLabel + " aplicado para " + wallets.size() + " carteiras.");
    }

}