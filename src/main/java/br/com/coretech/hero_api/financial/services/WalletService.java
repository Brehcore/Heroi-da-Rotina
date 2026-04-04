package br.com.coretech.hero_api.financial.services;

import br.com.coretech.hero_api.financial.dtos.WalletResponseDTO;
import br.com.coretech.hero_api.financial.entities.MoneyTransaction;
import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.entities.TokenTransaction;
import br.com.coretech.hero_api.financial.enums.TransactionType;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import br.com.coretech.hero_api.mappers.HeroMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
     * Adiciona fichas à carteira do menor e registra o histórico.
     */
    @Transactional
    public void TokenDeposit(Long minorId, Integer amount, String motive) {
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

}