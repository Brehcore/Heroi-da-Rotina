package br.com.coretech.hero_api.screentime.controllers;

import br.com.coretech.hero_api.screentime.dtos.ScreenTimeConfigDTO;
import br.com.coretech.hero_api.screentime.services.ScreenTimeConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@Tag(name = "Configuração do tempo de tela", description = "Gerenciamento das regras e limites de tempo de tela") // Tag corrigida para o padrão da documentação
@RestController
@RequestMapping("/api/screentime/config")
@RequiredArgsConstructor
public class ScreenTimeConfigController {

    private final ScreenTimeConfigService configService;

    @Operation(
            summary = "Buscar configuração",
            description = "Recupera os limites diários e a cotação de minutos por ficha de um menor específico."
    )
    @GetMapping("/minor/{minorId}")
    public ResponseEntity<ScreenTimeConfigDTO> getConfig(@PathVariable Long minorId) {
        return ResponseEntity.ok(configService.getConfig(minorId));
    }

    @Operation(
            summary = "Atualizar configuração",
            description = "Define ou altera manualmente os limites de tempo de domingo a sábado e o valor de minutos por ficha."
    )
    @PutMapping("/minor/{minorId}")
    public ResponseEntity<ScreenTimeConfigDTO> updateConfig(
            @PathVariable Long minorId,
            @RequestBody ScreenTimeConfigDTO dto) {
        return ResponseEntity.ok(configService.saveOrUpdateConfig(minorId, dto));
    }
}