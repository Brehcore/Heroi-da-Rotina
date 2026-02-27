package br.com.coretech.hero_api.tasks.services;

import br.com.coretech.hero_api.financial.services.WalletService;
import br.com.coretech.hero_api.tasks.dtos.TaskCreateDTO;
import br.com.coretech.hero_api.tasks.dtos.TaskResponseDTO;
import br.com.coretech.hero_api.tasks.entities.Task;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import br.com.coretech.hero_api.tasks.repositories.TaskRepository;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    /**
     * Cria uma nova tarefa atribuída a um menor.
     */
    @Transactional
    public TaskResponseDTO createTask(TaskCreateDTO dto) {
        User menor = userRepository.findById(dto.getMinorId())
                .orElseThrow(() -> new RuntimeException("Menor não encontrado com ID: " + dto.getMinorId()));

        User monitor = null;
        if (dto.getMonitorCreatorId() != null) {
            monitor = userRepository.findById(dto.getMonitorCreatorId())
                    .orElse(null);
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setTokenRaward(dto.getTokenReward());
        task.setMinor(menor);
        task.setMonitorCreator(monitor);
        task.setStatus(TaskStatus.PENDING);

        task = taskRepository.save(task);
        return TaskResponseDTO.fromEntity(task);
    }

    /**
     * Ação do MENOR: Marca a tarefa como feita.
     * Muda o status para CONCLUIDA, indicando que aguarda aprovação.
     */
    @Transactional
    public void completeTask(Long tarefaId) {
        Task task = taskRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada com ID: " + tarefaId));

        // Regra de negócio: só pode concluir se estiver PENDENTE
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new RuntimeException("Apenas tarefas PENDENTES podem ser concluídas.");
        }

        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);
    }

    /**
     * Ação do MONITOR: Aprova a tarefa e deposita as fichas na carteira do menor.
     */
    @Transactional
    public void aproveTask(Long tarefaId) {
        Task task = taskRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada com ID: " + tarefaId));

        // Regra de negócio: só pode aprovar se o menor tiver marcado como CONCLUIDA
        if (task.getStatus() != TaskStatus.COMPLETED) {
            throw new RuntimeException("A task precisa estar CONCLUIDA para ser aprovada.");
        }

        task.setStatus(TaskStatus.APPROVED);
        taskRepository.save(task);

        // Se houver recompensa em fichas, integra com o WalletService
        if (task.getTokenRaward() != null && task.getTokenRaward() > 0) {
            walletService.TokenDeposit(
                    task.getMinor().getId(),
                    task.getTokenRaward(),
                    "Recompensa pela task: " + task.getTitle()
            );
        }
    }

    // --- Consultas (Read) ---

    public List<TaskResponseDTO> listForMinor(Long minorId) {
        return taskRepository.findAllByMinorId(minorId).stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> listPendingForMinor(Long minorId) {
        return taskRepository.findAllByMinorIdAndStatus(minorId, TaskStatus.PENDING).stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> listForApproval(Long familyId) {
        // Busca as tarefas que o menor já fez (CONCLUIDA) e o monitor precisa revisar
        return taskRepository.findAllByMinorFamilyIdAndStatus(familyId, TaskStatus.COMPLETED).stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}