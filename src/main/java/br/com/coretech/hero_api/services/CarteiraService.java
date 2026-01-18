package br.com.coretech.hero_api.services;

import br.com.coretech.hero_api.entities.Carteira;
import br.com.coretech.hero_api.entities.TransacaoFicha;
import br.com.coretech.hero_api.enums.TipoTransacao;
import br.com.coretech.hero_api.repositories.CarteiraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CarteiraService {

    @Autowired
    private CarteiraRepository carteiraRepository;

    /**
     * Adiciona fichas à carteira do menor e registra o histórico.
     */
    @Transactional
    public void depositarFichas(Long menorId, Integer quantidade, String motivo) {
        Carteira carteira = carteiraRepository.findByMenorId(menorId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada para o menor ID: " + menorId));

        // 1. Atualiza saldo
        carteira.setSaldoFichas(carteira.getSaldoFichas() + quantidade);

        // 2. Cria registro de transação
        TransacaoFicha transacao = new TransacaoFicha();
        transacao.setCarteira(carteira);
        transacao.setTipo(TipoTransacao.CREDITO);
        transacao.setValor(quantidade);
        transacao.setMotivo(motivo);
        transacao.setData(LocalDateTime.now());

        // 3. Adiciona à lista (CascadeType.ALL vai salvar a transação automaticamente)
        carteira.getHistoricoFichas().add(transacao);

        // 4. Salva tudo
        carteiraRepository.save(carteira);
    }
}