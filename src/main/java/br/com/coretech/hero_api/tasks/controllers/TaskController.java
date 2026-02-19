package br.com.coretech.hero_api.tasks.controllers;

import br.com.coretech.hero_api.tasks.dtos.TaskCreateDTO;
import br.com.coretech.hero_api.tasks.dtos.TaskResponseDTO;
import br.com.coretech.hero_api.tasks.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST responsável por gerenciar operações relacionadas às tarefas dos menores.
 * Fornece endpoints para criação, atualização de status e consultas de tarefas tanto para o
 * aplicativo Android (usado pelos menores) quanto para a interface web Angular (usada pelos monitores).
 */
@RestController
@RequestMapping("/api/tarefas")
@CrossOrigin("*") // Libera acesso para o Angular
public class TaskController {

    @Autowired
    private TaskService taskService;

    // --- Ações de Criação ---

    /**
     * Cria uma nova tarefa no sistema.
     *
     * @param dto Dados da tarefa a ser criada
     * @return ResponseEntity com a tarefa criada e status 201 (CREATED)
     */
    @PostMapping
    public ResponseEntity<TaskResponseDTO> criar(@RequestBody TaskCreateDTO dto) {
        TaskResponseDTO novaTarefa = taskService.criarTarefa(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
    }

    // --- Ações de Fluxo (Mudança de Status) ---

    /**
     * Marca uma tarefa como concluída pelo menor através do app Android.
     *
     * @param id ID da tarefa a ser concluída
     * @return ResponseEntity com status 204 (NO CONTENT)
     */
    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Void> concluirTarefa(@PathVariable Long id) {
        taskService.concluirTarefa(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permite que um monitor aprove e efetue o pagamento de uma tarefa através da interface Angular.
     *
     * @param id ID da tarefa a ser aprovada
     * @return ResponseEntity com status 204 (NO CONTENT)
     */
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovarTarefa(@PathVariable Long id) {
        taskService.aprovarTarefa(id);
        return ResponseEntity.noContent().build();
    }

    // --- Consultas (Android - Menor) ---

    /**
     * Lista todas as tarefas de um menor específico (histórico completo).
     * Endpoint utilizado pelo aplicativo Android.
     *
     * @param menorId ID do menor
     * @return Lista de todas as tarefas do menor
     */
    @GetMapping("/menor/{menorId}")
    public ResponseEntity<List<TaskResponseDTO>> listarTodasDoMenor(@PathVariable Long menorId) {
        return ResponseEntity.ok(taskService.listarPorMenor(menorId));
    }

    /**
     * Lista apenas as tarefas pendentes de um menor específico.
     * Endpoint utilizado no dashboard principal do aplicativo Android.
     *
     * @param menorId ID do menor
     * @return Lista de tarefas pendentes do menor
     */
    @GetMapping("/menor/{menorId}/pendentes")
    public ResponseEntity<List<TaskResponseDTO>> listarPendentesDoMenor(@PathVariable Long menorId) {
        return ResponseEntity.ok(taskService.listarPendentesDoMenor(menorId));
    }

    // --- Consultas (Angular - Monitor) ---

    /**
     * Lista todas as tarefas que necessitam de aprovação para uma família específica.
     * Endpoint utilizado no dashboard do monitor na interface Angular.
     *
     * @param familiaId ID da família
     * @return Lista de tarefas aguardando aprovação
     */
    @GetMapping("/familia/{familiaId}/aprovar")
    public ResponseEntity<List<TaskResponseDTO>> listarParaAprovacao(@PathVariable Long familiaId) {
        return ResponseEntity.ok(taskService.listarParaAprovacao(familiaId));
    }
}