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
    public void depositarFichas(Long menorId, Integer quantidade, String motivo) {
        Wallet wallet = walletRepository.findByMenorId(menorId)
                .orElseThrow(() -> new RuntimeException("Wallet não encontrada para o menor ID: " + menorId));

        // 1. Atualiza saldo
        wallet.setSaldoFichas(wallet.getSaldoFichas() + quantidade);

        // 2. Cria registro de transação
        TokenTransaction transacao = new TokenTransaction();
        transacao.setWallet(wallet);
        transacao.setTipo(TransactionType.CREDITO);
        transacao.setValor(quantidade);
        transacao.setMotivo(motivo);
        transacao.setData(LocalDateTime.now());

        // 3. Adiciona à lista (CascadeType.ALL vai salvar a transação automaticamente)
        wallet.getHistoricoFichas().add(transacao);

        // 4. Salva tudo
        walletRepository.save(wallet);
    }
}