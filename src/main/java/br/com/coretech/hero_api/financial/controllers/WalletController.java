package br.com.coretech.hero_api.financial.controllers;

import br.com.coretech.hero_api.financial.dtos.WalletResponseDTO;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST responsável por gerenciar operações relacionadas às carteiras dos menores.
 * Fornece endpoints para consulta de saldo e dados das carteiras.
 */
@RestController
@RequestMapping("/api/carteiras")
@CrossOrigin("*") // Libera acesso para o Angular
public class WalletController {
    

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private HeroMapper heroMapper;

    /**
     * Busca o saldo e dados da carteira de um menor específico.
     *
     * @param menorId ID do menor para buscar a carteira
     * @return ResponseEntity contendo os dados da carteira se encontrada, ou status 404 se não existir
     */
    @GetMapping("/menor/{menorId}")
    public ResponseEntity<WalletResponseDTO> buscarCarteira(@PathVariable Long menorId) {
        return walletRepository.findByMenorId(menorId)
                .map(heroMapper::toCarteiraDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // OBS: O histórico de transações (extrato) já é complexo.
    // TODO: criar um endpoint separado depois se a lista de transações ficar muito grande dentro do objeto Wallet.
}