package br.com.coretech.hero_api.tasks.controllers;

import br.com.coretech.hero_api.tasks.dtos.TaskCreateDTO;
import br.com.coretech.hero_api.tasks.dtos.TaskRejectDTO;
import br.com.coretech.hero_api.tasks.dtos.TaskResponseDTO;
import br.com.coretech.hero_api.tasks.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tarefas", description = "Responsável pelas operações relacionadas a tarefas")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Criar tarefa", description = "Cria uma nova tarefa no sistema.")
    @PostMapping
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<TaskResponseDTO> create(@RequestBody TaskCreateDTO dto) {
        TaskResponseDTO newTask = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    @Operation(summary = "Remove uma tarefa", description = "Remove uma tarefa pelo id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MONITOR')")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Aprovar tarefa", description = "Permite que um monitor aprove e efetue o pagamento de uma tarefa")
    @PatchMapping("/{id}/approve")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> approveTask(@PathVariable Long id) {
        taskService.aproveTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reprovar tarefa", description = "Reprova uma tarefa concluída, enviando um motivo e permitindo que o menor a refaça.")
    @PatchMapping("/{id}/reject")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> rejectTask(@PathVariable Long id, @RequestBody TaskRejectDTO dto) {
        taskService.rejectTask(id, dto.getReason());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todas as tarefas", description = "Lista todas as tarefas paginadas para uma família específica.")
    @GetMapping("/family/{familyId}")
    @PreAuthorize("hasRole('MONITOR')")
    public ResponseEntity<Page<TaskResponseDTO>> listAllTasks(
            @PathVariable Long familyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(taskService.listAllTasks(familyId, pageable));
    }

    @Operation(summary = "Listar tarefas para aprovação", description = "Lista todas as tarefas que necessitam de aprovação para uma família específica.")
    @GetMapping("/family/{familyId}/approve")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<List<TaskResponseDTO>>listForApproval(@PathVariable Long familyId) {
        return ResponseEntity.ok(taskService.listForApproval(familyId));
    }

    //TODO: Editar para paginação aqui e no serviço
    @Operation(summary = "Listar todas as tarefas", description = "Lista todas as tarefas de um menor específico.")
    @GetMapping("/minor/{minorId}")
    @PreAuthorize( "isAuthenticated()")
    public ResponseEntity<List<TaskResponseDTO>> listAllTasksForMinor(@PathVariable Long minorId) {
        return ResponseEntity.ok(taskService.listForMinor(minorId));
    }

    @Operation(summary = "Marcar concluída", description = "Marca uma tarefa como concluída pelo menor")
    @PatchMapping("/{id}/conclude")
    @PreAuthorize( "isAuthenticated()")
    public ResponseEntity<Void> completeTask(@PathVariable Long id) {
        taskService.completeTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar tarefas pendentes", description = "Lista apenas as tarefas pendentes de um menor específico.")
    @GetMapping("/minor/{minorId}/pending")
    @PreAuthorize( "isAuthenticated()")
    public ResponseEntity<List<TaskResponseDTO>> listAllPendingTasks(@PathVariable Long minorId) {
        return ResponseEntity.ok(taskService.listPendingForMinor(minorId));
    }
}