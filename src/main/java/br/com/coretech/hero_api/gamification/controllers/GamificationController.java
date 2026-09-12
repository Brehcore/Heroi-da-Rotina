package br.com.coretech.hero_api.gamification.controllers;

import br.com.coretech.hero_api.gamification.dtos.GamificationResponseDTO;
import br.com.coretech.hero_api.gamification.services.GamificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
@Tag(name = "Gamificação", description = "Endpoints de níveis, XP e progresso do menor")
public class GamificationController {

    private final GamificationService gamificationService;

    @Operation(summary = "Consultar XP e Nível do Menor", description = "Retorna o nível atual, XP acumulado, meta para o próximo nível e porcentagem calculada.")
    @GetMapping("/minor/{minorId}")
    @PreAuthorize("hasAnyRole('MINOR', 'MONITOR')")
    public ResponseEntity<GamificationResponseDTO> getMinorGamification(@PathVariable Long minorId) {
        return ResponseEntity.ok(gamificationService.getGamificationByMinorId(minorId));
    }
}