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
@RequestMapping("/api/tasks")
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
    public ResponseEntity<TaskResponseDTO> create(@RequestBody TaskCreateDTO dto) {
        TaskResponseDTO newTask = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    // --- Ações de Fluxo (Mudança de Status) ---

    /**
     * Marca uma tarefa como concluída pelo menor através do app Android.
     *
     * @param id ID da tarefa a ser concluída
     * @return ResponseEntity com status 204 (NO CONTENT)
     */
    @PatchMapping("/{id}/conclude")
    public ResponseEntity<Void> completeTask(@PathVariable Long id) {
        taskService.completeTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permite que um monitor aprove e efetue o pagamento de uma tarefa através da interface Angular.
     *
     * @param id ID da tarefa a ser aprovada
     * @return ResponseEntity com status 204 (NO CONTENT)
     */
    @PatchMapping("/{id}/approve")
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
    @GetMapping("/minor/{minorId}/pending")
    public ResponseEntity<List<TaskResponseDTO>> listAllPendingTasks(@PathVariable Long minorId) {
        return ResponseEntity.ok(taskService.listPendingForMinor(minorId));
    }

    // --- Consultas (Angular - Monitor) ---

    /**
     * Lista todas as tarefas que necessitam de aprovação para uma família específica.
     * Endpoint utilizado no dashboard do monitor na interface Angular.
     *
     * @param familyId ID da família
     * @return Lista de tarefas aguardando aprovação
     */
    @GetMapping("/family/{familyId}/approve")
    public ResponseEntity<List<TaskResponseDTO>> listForApproval(@PathVariable Long familyId) {
        return ResponseEntity.ok(taskService.listForApproval(familyId));
    }
}