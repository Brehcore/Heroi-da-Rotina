package br.com.coretech.hero_api.financial.transaction.service;

import br.com.coretech.hero_api.financial.transaction.exception.InsufficientBalanceException;
import br.com.coretech.hero_api.exceptions.ResourceNotFoundException;
import br.com.coretech.hero_api.financial.transaction.dto.TransactionDTO;
import br.com.coretech.hero_api.financial.wallet.entity.MoneyTransaction;
import br.com.coretech.hero_api.financial.transaction.entity.TokenTransaction;
import br.com.coretech.hero_api.financial.wallet.entity.Wallet;
import br.com.coretech.hero_api.financial.transaction.enums.TransactionType;
import br.com.coretech.hero_api.financial.transaction.repository.TokenTransactionRepository;
import br.com.coretech.hero_api.financial.wallet.repository.WalletRepository;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.utils.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final WalletRepository walletRepository;
    private final TokenTransactionRepository tokenTransactionRepository;
    private final HeroMapper heroMapper;
    private final EmailNotificationService emailService;

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
     * Converte uma quantidade específica de fichas em dinheiro com base na cotação da carteira.
     */
    @Transactional
    public void convertTokensToMoney(Long minorId, Integer tokensToConvert) {
        if (tokensToConvert == null || tokensToConvert <= 0) {
            throw new IllegalArgumentException("A quantidade de fichas para conversão deve ser maior que zero.");
        }

        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada para o dependente ID: " + minorId));

        int currentBalance = wallet.getTokenBalances() != null ? wallet.getTokenBalances() : 0;

        // Validação: não pode converter mais fichas do que possui
        double valorConvertido = getValorConvertido(tokensToConvert, currentBalance, wallet);

        // 1. Debita a quantidade solicitada e credita o valor em dinheiro
        wallet.setTokenBalances(currentBalance - tokensToConvert);
        wallet.setMoneyBalances(wallet.getMoneyBalances() + valorConvertido);

        // 2. Registrar histórico de Débito de Fichas
        TokenTransaction tokenTx = new TokenTransaction();
        tokenTx.setWallet(wallet);
        tokenTx.setType(TransactionType.DEBIT);
        tokenTx.setValue(tokensToConvert);
        tokenTx.setMotive("Conversão de " + tokensToConvert + " fichas em dinheiro");
        tokenTx.setDate(LocalDateTime.now());
        wallet.getHistoricalTokens().add(tokenTx);

        // 3. Registrar histórico de Crédito de Dinheiro
        MoneyTransaction moneyTx = new MoneyTransaction();
        moneyTx.setWallet(wallet);
        moneyTx.setType(TransactionType.CREDIT);
        moneyTx.setValue(valorConvertido);
        moneyTx.setMotive("Recebido da conversão de " + tokensToConvert + " fichas");
        moneyTx.setDate(LocalDateTime.now());
        wallet.getHistoricalMoney().add(moneyTx);

        walletRepository.save(wallet);
    }
    private static double getValorConvertido(Integer tokensToConvert, int currentBalance, Wallet wallet) {
        if (tokensToConvert > currentBalance) {
            throw new IllegalArgumentException(String.format(
                    "Saldo de fichas insuficiente. Saldo atual: %d fichas, solicitadas para conversão: %d fichas.",
                    currentBalance, tokensToConvert
            ));
        }

        Double cotacao = wallet.getTokenQuotation();
        if (cotacao == null || cotacao <= 0.0) {
            throw new IllegalStateException("A carteira não possui uma cotação de fichas válida configurada.");
        }

        return tokensToConvert * cotacao;
    }

    /**
     * Remove fichas da carteira (uso de tela direto).
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

}
