package br.com.coretech.hero_api.financial.wallet.controller;

import br.com.coretech.hero_api.financial.wallet.dto.InterestConfigDTO;
import br.com.coretech.hero_api.financial.wallet.dto.WalletResponseDTO;
import br.com.coretech.hero_api.financial.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Carteira", description = "Responsável por gerenciar operações relacionadas às carteiras dos menores")
@RestController
@RequestMapping("/api/wallets")
@CrossOrigin("*")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "Atualizar cotação", description = "Define quanto vale cada ficha (Ex: 1 ficha = R$ 1.00)")
    @PatchMapping("/minor/{minorId}/quotation")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> updateQuotation(@PathVariable Long minorId, @RequestParam Double value) {
        walletService.updateQuotation(minorId, value);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Configurar Juros", description = "Ativa/Desativa rendimento e define a taxa e frequência.")
    @PatchMapping("/minor/{minorId}/interest-config")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> configInterest(
            @PathVariable Long minorId,
            @RequestBody InterestConfigDTO dto) {

        walletService.updateInterestConfig(minorId, dto.rate(), dto.enabled(), dto.frequency());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Buscar Carteira", description = "Retorna os saldos, a cotação atual da ficha e as configurações de rendimento.")
    @GetMapping("/minor/{minorId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletResponseDTO> getWallet(@PathVariable Long minorId) {
        WalletResponseDTO walletDto = walletService.getWalletByMinorId(minorId);
        return ResponseEntity.ok(walletDto);
    }
}