package br.com.coretech.hero_api.financial.piggybank.controller;

import br.com.coretech.hero_api.financial.piggybank.dto.PiggyBankDashboardDTO;
import br.com.coretech.hero_api.financial.piggybank.dto.SavingsGoalCreateDTO;
import br.com.coretech.hero_api.financial.piggybank.dto.SavingsGoalDepositDTO;
import br.com.coretech.hero_api.financial.piggybank.dto.SavingsGoalResponseDTO;
import br.com.coretech.hero_api.financial.piggybank.dto.SavingsGoalWithdrawDTO;
import br.com.coretech.hero_api.financial.piggybank.service.SavingsGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/minor-portal/piggy-bank")
@RequiredArgsConstructor
@Tag(name = "Cofrinho do Menor", description = "Endpoints de metas de economia, cofrinho e educação financeira")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @Operation(summary = "Obter dados gerais do cofrinho", description = "Retorna visão consolidada de saldo livre, metas, rendimento e metas ativas.")
    @GetMapping("/minor/{minorId}")
    @PreAuthorize("hasAnyRole('MINOR', 'MONITOR')")
    public ResponseEntity<PiggyBankDashboardDTO> getDashboard(@PathVariable Long minorId) {
        return ResponseEntity.ok(savingsGoalService.getPiggyBankDashboard(minorId));
    }

    @Operation(summary = "Criar nova meta de economia", description = "Cadastra uma meta ou item da wishlist para poupar dinheiro.")
    @PostMapping("/minor/{minorId}/goals")
    @PreAuthorize("hasAnyRole('MINOR', 'MONITOR')")
    public ResponseEntity<SavingsGoalResponseDTO> createGoal(
            @PathVariable Long minorId,
            @Valid @RequestBody SavingsGoalCreateDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(savingsGoalService.createGoal(minorId, dto));
    }

    @Operation(summary = "Guardar dinheiro na meta", description = "Transfere saldo em dinheiro livre da carteira para uma meta de economia específica.")
    @PostMapping("/minor/{minorId}/goals/{goalId}/deposit")
    @PreAuthorize("hasAnyRole('MINOR', 'MONITOR')")
    public ResponseEntity<SavingsGoalResponseDTO> depositToGoal(
            @PathVariable Long minorId,
            @PathVariable Long goalId,
            @Valid @RequestBody SavingsGoalDepositDTO dto
    ) {
        return ResponseEntity.ok(savingsGoalService.depositToGoal(minorId, goalId, dto));
    }

    @Operation(summary = "Resgatar dinheiro da meta", description = "Devolve saldo da meta de volta para a carteira livre do menor.")
    @PostMapping("/minor/{minorId}/goals/{goalId}/withdraw")
    @PreAuthorize("hasAnyRole('MINOR', 'MONITOR')")
    public ResponseEntity<SavingsGoalResponseDTO> withdrawFromGoal(
            @PathVariable Long minorId,
            @PathVariable Long goalId,
            @Valid @RequestBody SavingsGoalWithdrawDTO dto
    ) {
        return ResponseEntity.ok(savingsGoalService.withdrawFromGoal(minorId, goalId, dto));
    }
}