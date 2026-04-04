package br.com.coretech.hero_api.tasks.controllers;

import br.com.coretech.hero_api.tasks.dtos.TaskCreateDTO;
import br.com.coretech.hero_api.tasks.dtos.TaskResponseDTO;
import br.com.coretech.hero_api.tasks.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST responsável por gerenciar operações relacionadas às tarefas dos menores.
 * Fornece endpoints para criação, atualização de status e consultas de tarefas tanto para o
 * aplicativo Android (usado pelos menores) quanto para a interface web Angular (usada pelos monitores).
 */
@Tag(name = "Tarefas", description = "Responsável pelas operações relacionadas a tarefas")
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin("*")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Cria uma nova tarefa no sistema.
     * @apiNote Requer autenticação ROLE_MONITOR
     * @param dto Dados da tarefa a ser criada
     * @return ResponseEntity com a tarefa criada e status 201 (CREATED)
     */
    @Operation(summary = "Criar tarefa", description = "Cria uma nova tarefa no sistema.")
    @PostMapping
    @PreAuthorize( "hasRole('ROLE_MONITOR')")
    public ResponseEntity<TaskResponseDTO> create(@RequestBody TaskCreateDTO dto) {
        TaskResponseDTO newTask = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    /**
     * Marca uma tarefa como concluída pelo menor.
     *
     * @param id ID da tarefa a ser concluída
     * @return ResponseEntity com status 204 (NO CONTENT)
     */
    @Operation(summary = "Marcar concluída", description = "Marca uma tarefa como concluída pelo menor")
    @PatchMapping("/{id}/conclude")
    public ResponseEntity<Void> completeTask(@PathVariable Long id) {
        taskService.completeTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permite que um monitor aprove e efetue o pagamento de uma tarefa através da interface Angular.
     * @apiNote Requer autenticação ROLE_MONITOR
     * @param id ID da tarefa a ser aprovada
     * @return ResponseEntity com status 204 (NO CONTENT)
     */
    @Operation(summary = "Aprovar tarefa", description = "Permite que um monitor aprove e efetue o pagamento de uma tarefa")
    @PatchMapping("/{id}/approve")
    @PreAuthorize( "hasRole('ROLE_MONITOR')")
    public ResponseEntity<Void> approveTask(@PathVariable Long id) {
        taskService.aproveTask(id);
        return ResponseEntity.noContent().build();
    }

    // --- Consultas (Android - Menor) ---

    /**
     * Lista todas as tarefas de um menor específico (histórico completo).
     * Endpoint utilizado pelo aplicativo Android.
     *
     * @param minorId ID do menor
     * @return Lista de todas as tarefas do menor
     */
    @Operation(summary = "Listar todas as tarefas", description = "Lista todas as tarefas de um menor específico.")
    @GetMapping("/minor/{minorId}")
    public ResponseEntity<List<TaskResponseDTO>> listAllTasksForMinor(@PathVariable Long minorId) {
        return ResponseEntity.ok(taskService.listForMinor(minorId));
    }

    /**
     * Lista apenas as tarefas pendentes de um menor específico.
     * Endpoint utilizado no dashboard principal do aplicativo Android.
     *
     * @param minorId ID do menor
     * @return Lista de tarefas pendentes do menor
     */
    @Operation(summary = "Listar tarefas pendentes", description = "Lista apenas as tarefas pendentes de um menor específico.")
    @GetMapping("/minor/{minorId}/pending")
    public ResponseEntity<List<TaskResponseDTO>> listAllPendingTasks(@PathVariable Long minorId) {
        return ResponseEntity.ok(taskService.listPendingForMinor(minorId));
    }

    /**
     * Lista todas as tarefas que necessitam de aprovação para uma família específica.
     * Endpoint utilizado no dashboard do monitor na interface Angular.
     * @apiNote Requer autenticação ROLE_MONITOR
     * @param familyId ID da família
     * @return Lista de tarefas aguardando aprovação
     */
    @Operation(summary = "Listar tarefas para aprovação", description = "Lista todas as tarefas que necessitam de aprovação para uma família específica.")
    @GetMapping("/family/{familyId}/approve")
    @PreAuthorize( "hasRole('ROLE_MONITOR')")
    public ResponseEntity<List<TaskResponseDTO>> listForApproval(@PathVariable Long familyId) {
        return ResponseEntity.ok(taskService.listForApproval(familyId));
    }
}