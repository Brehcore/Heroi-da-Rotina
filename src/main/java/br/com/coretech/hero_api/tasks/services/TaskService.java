package br.com.coretech.hero_api.tasks.services;

import br.com.coretech.hero_api.financial.services.WalletService;
import br.com.coretech.hero_api.tasks.dtos.TaskCreateDTO;
import br.com.coretech.hero_api.tasks.dtos.TaskResponseDTO;
import br.com.coretech.hero_api.tasks.entities.Task;
import br.com.coretech.hero_api.users.entities.Usuario;
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
    public TaskResponseDTO criarTarefa(TaskCreateDTO dto) {
        Usuario menor = userRepository.findById(dto.getMenorId())
                .orElseThrow(() -> new RuntimeException("Menor não encontrado com ID: " + dto.getMenorId()));

        Usuario monitor = null;
        if (dto.getMonitorCriadorId() != null) {
            monitor = userRepository.findById(dto.getMonitorCriadorId())
                    .orElse(null);
        }

        Task task = new Task();
        task.setTitulo(dto.getTitulo());
        task.setDescricao(dto.getDescricao());
        task.setRecompensaFichas(dto.getRecompensaFichas());
        task.setMenor(menor);
        task.setMonitorCriador(monitor);
        task.setStatus(TaskStatus.PENDENTE);

        task = taskRepository.save(task);
        return TaskResponseDTO.fromEntity(task);
    }

    /**
     * Ação do MENOR: Marca a tarefa como feita.
     * Muda o status para CONCLUIDA, indicando que aguarda aprovação.
     */
    @Transactional
    public void concluirTarefa(Long tarefaId) {
        Task task = taskRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada com ID: " + tarefaId));

        // Regra de negócio: só pode concluir se estiver PENDENTE
        if (task.getStatus() != TaskStatus.PENDENTE) {
            throw new RuntimeException("Apenas tarefas PENDENTES podem ser concluídas.");
        }

        task.setStatus(TaskStatus.CONCLUIDA);
        taskRepository.save(task);
    }

    /**
     * Ação do MONITOR: Aprova a tarefa e deposita as fichas na carteira do menor.
     */
    @Transactional
    public void aprovarTarefa(Long tarefaId) {
        Task task = taskRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada com ID: " + tarefaId));

        // Regra de negócio: só pode aprovar se o menor tiver marcado como CONCLUIDA
        if (task.getStatus() != TaskStatus.CONCLUIDA) {
            throw new RuntimeException("A task precisa estar CONCLUIDA para ser aprovada.");
        }

        task.setStatus(TaskStatus.APROVADA);
        taskRepository.save(task);

        // Se houver recompensa em fichas, integra com o WalletService
        if (task.getRecompensaFichas() != null && task.getRecompensaFichas() > 0) {
            walletService.depositarFichas(
                    task.getMenor().getId(),
                    task.getRecompensaFichas(),
                    "Recompensa pela task: " + task.getTitulo()
            );
        }
    }

    // --- Consultas (Read) ---

    public List<TaskResponseDTO> listarPorMenor(Long menorId) {
        return taskRepository.findAllByMenorId(menorId).stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> listarPendentesDoMenor(Long menorId) {
        return taskRepository.findAllByMenorIdAndStatus(menorId, TaskStatus.PENDENTE).stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> listarParaAprovacao(Long familiaId) {
        // Busca as tarefas que o menor já fez (CONCLUIDA) e o monitor precisa revisar
        return taskRepository.findAllByMenorFamiliaIdAndStatus(familiaId, TaskStatus.CONCLUIDA).stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}