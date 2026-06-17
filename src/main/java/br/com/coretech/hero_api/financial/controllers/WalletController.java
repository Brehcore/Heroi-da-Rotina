package br.com.coretech.hero_api.financial.controllers;

import br.com.coretech.hero_api.financial.dtos.DeductTokensRequestDTO;
import br.com.coretech.hero_api.financial.dtos.InterestConfigDTO;
import br.com.coretech.hero_api.financial.dtos.TransactionDTO;
import br.com.coretech.hero_api.financial.dtos.WalletResponseDTO;
import br.com.coretech.hero_api.financial.services.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * Busca os dados completos da carteira de um menor (Saldos, Cotação e Config. de Juros)
     * @apiNote Pode ser acessado pelo MENOR (para ver seus dados) ou pelo MONITOR.
     */
    @Operation(summary = "Buscar Carteira", description = "Retorna os saldos, a cotação atual da ficha e as configurações de rendimento.")
    @GetMapping("/minor/{minorId}")
    @PreAuthorize("isAuthenticated()") // Permite que tanto o Monitor quanto o Menor logado possam ver
    public ResponseEntity<WalletResponseDTO> getWallet(@PathVariable Long minorId) {
        WalletResponseDTO walletDto = walletService.getWalletByMinorId(minorId);
        return ResponseEntity.ok(walletDto);
    }

    @Operation(summary = "Buscar Histórico", description = "Retorna o histórico financeiro do menor")
    @GetMapping("/minor/{minorId}/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TransactionDTO>> getMinorTransactionalHistory(@PathVariable Long minorId, Pageable pageable) {
            Page<TransactionDTO> transactions = walletService.getMinorTransactionalHistory(minorId, pageable);
            return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Adicionar fichas", description = "Monitor adiciona fichas à carteira do menor.")
    @PostMapping("/minor/{minorId}/deposit-tokens")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> depositTokens(@PathVariable Long minorId, @RequestParam Integer amount, @RequestParam String motive) {
        walletService.tokenDeposit(minorId, amount, motive);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remover fichas", description = "Monitor remove fichas da carteira do menor.")
    @PostMapping("/minor/{minorId}/deduct-tokens")
    @PreAuthorize("hasRole('MONITOR')")
    public ResponseEntity<Void> deductToken(@PathVariable Long minorId, @Valid @RequestBody DeductTokensRequestDTO dto) {
        walletService.tokenDeduct(minorId, dto.getAmount(), dto.getMotive());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Atualizar cotação", description = "Define quanto vale cada ficha (Ex: 1 ficha = R$ 1.00)")
    @PatchMapping("/minor/{minorId}/quotation")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> updateQuotation(@PathVariable Long minorId, @RequestParam Double value) {
        walletService.updateQuotation(minorId, value);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Converter fichas", description = "Transforma todas as fichas em saldo de dinheiro.")
    @PostMapping("/minor/{minorId}/convert")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> convertTokens(@PathVariable Long minorId) {
        walletService.convertTokensToMoney(minorId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Configurar Juros", description = "Ativa/Desativa rendimento e define a taxa e frequência.")
    @PatchMapping("/minor/{minorId}/interest-config")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> configInterest(
            @PathVariable Long minorId,
            @RequestBody InterestConfigDTO dto) {

        walletService.updateInterestConfig(minorId, dto.getRate(), dto.getEnabled(), dto.getFrequency());
        return ResponseEntity.ok().build();
    }

}