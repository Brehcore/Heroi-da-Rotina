package br.com.coretech.hero_api.controllers;

import br.com.coretech.hero_api.dtos.TarefaCreateDTO;
import br.com.coretech.hero_api.dtos.TarefaResponseDTO;
import br.com.coretech.hero_api.services.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
@CrossOrigin("*") // Libera acesso para o Angular
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    // --- Ações de Criação ---

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criar(@RequestBody TarefaCreateDTO dto) {
        TarefaResponseDTO novaTarefa = tarefaService.criarTarefa(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
    }

    // --- Ações de Fluxo (Mudança de Status) ---

    // Menor marca como concluída (Android)
    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Void> concluirTarefa(@PathVariable Long id) {
        tarefaService.concluirTarefa(id);
        return ResponseEntity.noContent().build();
    }

    // Monitor aprova e paga (Angular)
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovarTarefa(@PathVariable Long id) {
        tarefaService.aprovarTarefa(id);
        return ResponseEntity.noContent().build();
    }

    // --- Consultas (Android - Menor) ---

    // Todas as tarefas do menor (Histórico)
    @GetMapping("/menor/{menorId}")
    public ResponseEntity<List<TarefaResponseDTO>> listarTodasDoMenor(@PathVariable Long menorId) {
        return ResponseEntity.ok(tarefaService.listarPorMenor(menorId));
    }

    // Apenas pendentes (Dashboard Principal do App)
    @GetMapping("/menor/{menorId}/pendentes")
    public ResponseEntity<List<TarefaResponseDTO>> listarPendentesDoMenor(@PathVariable Long menorId) {
        return ResponseEntity.ok(tarefaService.listarPendentesDoMenor(menorId));
    }

    // --- Consultas (Angular - Monitor) ---

    // Tarefas que precisam de aprovação (Dashboard do Monitor)
    @GetMapping("/familia/{familiaId}/aprovar")
    public ResponseEntity<List<TarefaResponseDTO>> listarParaAprovacao(@PathVariable Long familiaId) {
        return ResponseEntity.ok(tarefaService.listarParaAprovacao(familiaId));
    }
}