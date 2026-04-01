package br.com.coretech.hero_api.financial.controllers;

import br.com.coretech.hero_api.financial.dtos.WalletResponseDTO;
import br.com.coretech.hero_api.financial.services.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST responsável por gerenciar operações relacionadas às carteiras dos menores.
 * Fornece endpoints para consulta de saldo e dados das carteiras.
 */
@Tag(name = "Carteira", description = "Responsável por gerenciar operações relacionadas às carteiras dos menores")
@RestController
@RequestMapping("/api/wallets")
@CrossOrigin("*")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * Busca o saldo e dados da carteira de um menor específico.
     *
     * @param minorId ID do menor para buscar a carteira
     * @return ResponseEntity contendo os dados da carteira se encontrada, ou status 404 se não existir
     */
    @Operation(summary = "Buscar saldo", description = "Busca o saldo e dados da carteira de um menor específico.")
    @GetMapping("/minor/{minorId}")
    public ResponseEntity<WalletResponseDTO> searchWallets(@PathVariable Long minorId) {
        // O Controller apenas repassa a ordem e devolve a resposta
        return ResponseEntity.ok(walletService.findByMinorId(minorId));
    }
}