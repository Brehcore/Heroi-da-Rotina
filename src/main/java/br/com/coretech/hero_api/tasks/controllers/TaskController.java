package br.com.coretech.hero_api.tasks.controllers;

import br.com.coretech.hero_api.tasks.dtos.TaskCreateDTO;
import br.com.coretech.hero_api.tasks.dtos.TaskRejectDTO;
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
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Cria uma nova tarefa no sistema.
     * @apiNote Requer autenticação MONITOR
     * @param dto Dados da tarefa a ser criada
     * @return ResponseEntity com a tarefa criada e status 201 (CREATED)
     */
    @Operation(summary = "Criar tarefa", description = "Cria uma nova tarefa no sistema.")
    @PostMapping
    @PreAuthorize( "hasRole('MONITOR')")
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
    @PreAuthorize( "isAuthenticated()")
    public ResponseEntity<Void> completeTask(@PathVariable Long id) {
        taskService.completeTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permite que um monitor aprove e efetue o pagamento de uma tarefa através da interface Angular.
     * @apiNote Requer autenticação MONITOR
     * @param id ID da tarefa a ser aprovada
     * @return ResponseEntity com status 204 (NO CONTENT)
     */
    @Operation(summary = "Aprovar tarefa", description = "Permite que um monitor aprove e efetue o pagamento de uma tarefa")
    @PatchMapping("/{id}/approve")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> approveTask(@PathVariable Long id) {
        taskService.aproveTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permite que um monitor reprove uma tarefa, enviando um motivo.
     * A tarefa voltará para o status PENDING para o menor refazer.
     * @apiNote Requer autenticação MONITOR
     * @param id ID da tarefa a ser reprovada
     * @param dto Corpo contendo o motivo da reprovação
     * @return ResponseEntity com status 204 (NO CONTENT)
     */
    @Operation(summary = "Reprovar tarefa", description = "Reprova uma tarefa concluída, enviando um motivo e permitindo que o menor a refaça.")
    @PatchMapping("/{id}/reject")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> rejectTask(@PathVariable Long id, @RequestBody TaskRejectDTO dto) {
        taskService.rejectTask(id, dto.getReason());
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
    @PreAuthorize( "isAuthenticated()")
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
    @PreAuthorize( "isAuthenticated()")
    public ResponseEntity<List<TaskResponseDTO>> listAllPendingTasks(@PathVariable Long minorId) {
        return ResponseEntity.ok(taskService.listPendingForMinor(minorId));
    }

    /**
     * Lista todas as tarefas que necessitam de aprovação para uma família específica.
     * Endpoint utilizado no dashboard do monitor na interface Angular.
     * @apiNote Requer autenticação MONITOR
     * @param familyId ID da família
     * @return Lista de tarefas aguardando aprovação
     */
    @Operation(summary = "Listar tarefas para aprovação", description = "Lista todas as tarefas que necessitam de aprovação para uma família específica.")
    @GetMapping("/family/{familyId}/approve")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<List<TaskResponseDTO>>listForApproval(@PathVariable Long familyId) {
        return ResponseEntity.ok(taskService.listForApproval(familyId));
    }
}