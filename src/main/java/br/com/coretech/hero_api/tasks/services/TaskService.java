package br.com.coretech.hero_api.tasks.services;

import br.com.coretech.hero_api.financial.services.WalletService;
import br.com.coretech.hero_api.gamification.services.GamificationService;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.tasks.dtos.TaskCreateDTO;
import br.com.coretech.hero_api.tasks.dtos.TaskResponseDTO;
import br.com.coretech.hero_api.tasks.entities.Task;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import br.com.coretech.hero_api.tasks.repositories.TaskRepository;
import br.com.coretech.hero_api.users.enums.UserRole;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import br.com.coretech.hero_api.utils.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final GamificationService  gamificationService;
    private final HeroMapper heroMapper;
    private final EmailNotificationService emailService;

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
        task.setTokenReward(dto.getTokenReward());
        task.setMinor(menor);
        task.setMonitorCreator(monitor);
        task.setStatus(TaskStatus.PENDING);

        task = taskRepository.save(task);

        // DISPARO DO E-MAIL DE NOVA MISSÃO
        if (menor.getEmail() != null && !menor.getEmail().isEmpty()) {
            String assunto = "🚀 Nova Missão: " + task.getTitle();

            String corpoHtml = String.format("""
                <div style="font-family: Arial, sans-serif; background-color: #f4f7f6; padding: 20px;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
                        <div style="background-color: #8E44AD; padding: 20px; text-align: center; color: white;">
                            <h2 style="margin: 0;">🚀 Nova Missão Disponível!</h2>
                        </div>
                        <div style="padding: 30px; color: #333333; line-height: 1.6;">
                            <p style="font-size: 18px;">Preparado, <strong>%s</strong>?</p>
                            <p>O seu monitor acabou de enviar um novo desafio para você. Cumpra a missão para ganhar recompensas!</p>
                            <div style="background-color: #f4ebf9; border-left: 4px solid #8E44AD; border-radius: 4px; padding: 20px; margin: 25px 0;">
                                <h3 style="margin: 0 0 10px 0; color: #5b2c6f; font-size: 20px;">%s</h3>
                                <p style="margin: 0; font-size: 16px; font-weight: bold; color: #333;">
                                    Recompensa: <span style="color: #8E44AD;">🪙 %d Fichas</span>
                                </p>
                            </div>
                            <p style="text-align: center; color: #777; font-size: 14px;">Abra o aplicativo "Herói da Rotina", marque como concluída quando terminar e aguarde a aprovação!</p>
                        </div>
                    </div>
                </div>
                """, task.getMinor().getName(), task.getTitle(), task.getTokenReward());

            emailService.sendEmail(menor.getEmail(), assunto, corpoHtml);
        }

        return heroMapper.toTaskDTO(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Tarefa com ID :" + id + " não encontrada");
        }
        taskRepository.deleteById(id);
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
        // O servidor registra a hora exata que o botão foi clicado
        task.setCompletedDate(LocalDateTime.now());

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
        task.setApprovalDate(LocalDateTime.now());
        taskRepository.save(task);

        // 2. Concede XP na Gamificação (acontece para qualquer tarefa aprovada)
        gamificationService.grantXpForApprovedTask(
                task.getMinor().getId(),
                task.getTokenReward()
        );

        // 3. Se houver recompensa em fichas, integra com o WalletService e Notifica
        if (task.getTokenReward() != null && task.getTokenReward() > 0) {

            // Faz o depósito real no cofre
            walletService.tokenDeposit(
                    task.getMinor().getId(),
                    task.getTokenReward(),
                    "Recompensa pela task: " + task.getTitle()
            );

            // Dispara o e-mail de comemoração
            User menor = task.getMinor();
            if (UserRole.MINOR.equals(menor.getRole())) {
                String assunto = "🎉 Fichas na Conta! Parabéns pelo seu esforço!";
                String corpoHtml = String.format("""
                    <div style="font-family: Arial, sans-serif; background-color: #f4f7f6; padding: 20px;">
                        <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
                            <div style="background-color: #27AE60; padding: 20px; text-align: center; color: white;">
                                <h2 style="margin: 0;">🎉 Tarefa Aprovada!</h2>
                            </div>
                            <div style="padding: 30px; color: #333333; line-height: 1.6;">
                                <p style="font-size: 18px;">Oi, <strong>%s</strong>!</p>
                                <p>Excelente trabalho! A sua tarefa <strong>"%s"</strong> foi avaliada e aprovada.</p>
                                <div style="text-align: center; margin: 30px 0; padding: 20px; background-color: #eafaf1; border-radius: 8px;">
                                    <span style="font-size: 40px; font-weight: bold; color: #27AE60;">🪙 +%d</span>
                                    <p style="color: #229954; margin-top: 10px; font-weight: bold;">Fichas depositadas no seu cofre!</p>
                                </div>
                                <p style="text-align: center;">Continue assim para conquistar ainda mais recompensas!</p>
                            </div>
                        </div>
                    </div>
                    """, menor.getName(), task.getTitle(), task.getTokenReward());

                emailService.sendEmail(menor.getEmail(), assunto, corpoHtml);
            }
        }
    }

    /**
     * Ação do MONITOR: Reprova a tarefa e devolve para o menor refazer.
     */
    @Transactional
    public void rejectTask(Long tarefaId, String reason) {
        Task task = taskRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada com ID: " + tarefaId));

        // Regra de negócio: só pode reprovar se o menor tiver marcado como CONCLUIDA
        if (task.getStatus() != TaskStatus.COMPLETED) {
            throw new RuntimeException("A task precisa estar CONCLUIDA para ser reprovada.");
        }

        // Volta a tarefa para o estado inicial para o menor refazer
        task.setStatus(TaskStatus.PENDING);
        task.setRejectionReason(reason);
        task.setCompletedDate(null); // Reseta a data de conclusão, já que foi invalidada

        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> listForMinor(Long minorId) {
        return taskRepository.findAllByMinorId(minorId).stream()
                .map(heroMapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> listPendingForMinor(Long minorId) {
        return taskRepository.findAllByMinorIdAndStatus(minorId, TaskStatus.PENDING).stream()
                .map(heroMapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> listForApproval(Long familyId) {
        // Busca as tarefas que o menor já fez (CONCLUIDA) e o monitor precisa revisar
        return taskRepository.findAllByMinorFamiliesIdAndStatus(familyId, TaskStatus.COMPLETED).stream()
                .map(heroMapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> listAllTasks(Long familyId, Pageable pageable) {
        Page<Task> taskPage = taskRepository.findByMinor_Families_Id(familyId, pageable);
        return taskPage.map(heroMapper::toTaskDTO);
    }
}