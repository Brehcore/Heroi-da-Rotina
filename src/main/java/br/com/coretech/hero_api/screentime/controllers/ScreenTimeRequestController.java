package br.com.coretech.hero_api.screentime.controllers;

import br.com.coretech.hero_api.screentime.entities.ScreenTimeRequest;
import br.com.coretech.hero_api.screentime.services.ScreenTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Requisição do tempo de tela", description = "Gerenciamento do fluxo de solicitações e aprovações de tempo de tela")
@RestController
@RequestMapping("/api/screentime/request")
@RequiredArgsConstructor
public class ScreenTimeRequestController {

    private final ScreenTimeService screenTimeService;

    @Operation(
            summary = "Solicitar tempo de tela (Menor)",
            description = "Permite que o menor solicite minutos de tela. O sistema valida automaticamente o limite do dia e o saldo de fichas."
    )
    @PostMapping("/minor/{minorId}")
    public ResponseEntity<ScreenTimeRequest> requestScreenTime(
            @PathVariable Long minorId,
            @RequestParam Integer minutes) {
        return ResponseEntity.ok(screenTimeService.requestScreenTime(minorId, minutes));
    }

    @Operation(
            summary = "Aprovar solicitação (Monitor)",
            description = "Permite que um monitor aprove um pedido pendente. Ao aprovar, as fichas são debitadas da carteira do menor."
    )
    @PatchMapping("/{requestId}/approve")
    public ResponseEntity<Void> approveRequest(
            @PathVariable Long requestId,
            @RequestParam Long monitorId) {
        screenTimeService.approveRequest(requestId, monitorId);
        return ResponseEntity.ok().build();
    }
}