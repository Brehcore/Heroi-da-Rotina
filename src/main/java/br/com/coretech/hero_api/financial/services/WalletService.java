package br.com.coretech.hero_api.financial.services;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.entities.TokenTransaction;
import br.com.coretech.hero_api.financial.enums.TransactionType;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

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

        // 3. Adiciona à lista (CascadeType.ALL vai salvar a transação automaticamente)
        wallet.getHistoricalTokens().add(transaction);

        // 4. Salva tudo
        walletRepository.save(wallet);
    }
}