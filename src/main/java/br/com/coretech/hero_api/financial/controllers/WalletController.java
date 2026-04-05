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
        return ResponseEntity.ok(walletService.getWalletByMinorId(minorId));
    }

    @Operation(summary = "Adicionar fichas", description = "Monitor adiciona fichas à carteira do menor.")
    @PostMapping("/minor/{minorId}/deposit-tokens")
    public ResponseEntity<Void> depositTokens(@PathVariable Long minorId, @RequestParam Integer amount, @RequestParam String motive) {
        walletService.TokenDeposit(minorId, amount, motive);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Atualizar cotação", description = "Define quanto vale cada ficha (Ex: 1 ficha = R$ 1.00)")
    @PatchMapping("/minor/{minorId}/quotation")
    public ResponseEntity<Void> updateQuotation(@PathVariable Long minorId, @RequestParam Double value) {
        walletService.updateQuotation(minorId, value);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Converter fichas", description = "Transforma todas as fichas em saldo de dinheiro.")
    @PostMapping("/minor/{minorId}/convert")
    public ResponseEntity<Void> convertTokens(@PathVariable Long minorId) {
        walletService.convertTokensToMoney(minorId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Configurar Juros", description = "Ativa/Desativa rendimento e define a taxa.")
    @PatchMapping("/minor/{minorId}/interest-config")
    public ResponseEntity<Void> configInterest(
            @PathVariable Long minorId,
            @RequestParam Double rate,
            @RequestParam Boolean enabled) {
        walletService.updateInterestConfig(minorId, rate, enabled);
        return ResponseEntity.ok().build();
    }

}