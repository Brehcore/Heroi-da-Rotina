package br.com.coretech.hero_api.screentime.controllers;

import br.com.coretech.hero_api.screentime.dtos.ScreenTimeResponseDTO;
import br.com.coretech.hero_api.screentime.dtos.TokenExchangeRequestDTO;
import br.com.coretech.hero_api.screentime.services.ScreenTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Requisição do tempo de tela", description = "Gerenciamento do fluxo de solicitações e aprovações de tempo de tela")
@RestController
@RequestMapping("/api/screentime/request")
@RequiredArgsConstructor
public class ScreenTimeRequestController {

    private final ScreenTimeService screenTimeService;

    @Operation(
            summary = "Trocar fichas por tempo de tela",
            description = "Permite que o menor solicite a troca de fichas por minutos de tela. O sistema calcula o tempo automaticamente, e valida o limite do dia e o saldo de fichas."
    )
    @PostMapping("/exchange-tokens")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ScreenTimeResponseDTO> exchangeTokensForTime(
            @RequestBody @Valid TokenExchangeRequestDTO requestDTO) {
        ScreenTimeResponseDTO response = screenTimeService.exchangeTokensForTime(requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Aprovar solicitação (Monitor)",
            description = "Permite que um monitor aprove um pedido pendente. Ao aprovar, as fichas são debitadas da carteira do menor."
    )
    @PatchMapping("/{requestId}/approve")
    @PreAuthorize("hasRole('MONITOR')")
    public ResponseEntity<Void> approveRequest(
            @PathVariable Long requestId,
            @RequestParam Long monitorId) {
        screenTimeService.approveRequest(requestId, monitorId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Listar solicitações pendentes (Monitor)",
            description = "Retorna uma lista de solicitações pendentes para alimentar as notificações."
    )
    @GetMapping("/family/{familyId}/pending")
    @PreAuthorize("hasRole('MONITOR')")
    public ResponseEntity<List<ScreenTimeResponseDTO>> getPendingRequests(@PathVariable Long familyId) {
        return ResponseEntity.ok(screenTimeService.getPendingRequestsForFamily(familyId));
    }

    @Operation(
            summary = "Recusar solicitação (Monitor)",
            description = "Permite que um monitor recuse um pedido de tempo de tela. Nenhuma ficha é descontada."
    )
    @PatchMapping("/{requestId}/reject")
    @PreAuthorize("hasRole('MONITOR')")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long requestId,
            @RequestParam Long monitorId) {
        screenTimeService.rejectRequest(requestId, monitorId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Ação via E-mail", description = "Processa aprovação ou rejeição vinda do clique no e-mail sem precisar logar.")
    @GetMapping("/email-action")
    public ResponseEntity<String> handleEmailAction(
            @RequestParam String token,
            @RequestParam String action,
            @RequestParam Long monitorId) {

        // Chama o service que devolve o HTML
        String htmlMessage = screenTimeService.processEmailAction(token, action, monitorId);

        // Retorna um HTML básico para a tela do navegador do Monitor não ficar feia
        String responseHtml = "<html><body style='text-align: center; font-family: sans-serif; padding-top: 50px;'>"
                + htmlMessage
                + "</body></html>";

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(responseHtml);
    }
}