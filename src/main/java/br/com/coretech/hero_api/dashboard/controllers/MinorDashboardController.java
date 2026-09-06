package br.com.coretech.hero_api.dashboard.controllers;

import br.com.coretech.hero_api.dashboard.dtos.MinorDashboardResponseDTO;
import br.com.coretech.hero_api.dashboard.services.MinorDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard do Menor", description = "Fornece a visão consolidada (BFF) para o portal do dependente")
@RestController
@RequestMapping("/api/minor-portal/dashboard")
@RequiredArgsConstructor
public class MinorDashboardController {

    private final MinorDashboardService minorDashboardService;

    @Operation(
            summary = "Obter dados do Dashboard",
            description = "Retorna os resumos agregados de Carteira, Tempo de Tela, Missões do dia e Gamificação para um menor específico."
    )
    @GetMapping("/{minorId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MinorDashboardResponseDTO> getDashboard(@PathVariable Long minorId) {
        MinorDashboardResponseDTO dashboard = minorDashboardService.getDashboard(minorId);
        return ResponseEntity.ok(dashboard);
    }
}