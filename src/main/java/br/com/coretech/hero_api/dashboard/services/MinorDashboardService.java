package br.com.coretech.hero_api.dashboard.services;

import br.com.coretech.hero_api.dashboard.dtos.*;
import br.com.coretech.hero_api.dashboard.mappers.DashboardMapper;
import br.com.coretech.hero_api.exceptions.ResourceNotFoundException;
import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import br.com.coretech.hero_api.gamification.entities.UserGamification;
import br.com.coretech.hero_api.gamification.repositories.UserGamificationRepository;
import br.com.coretech.hero_api.screentime.entities.ScreenTimeConfig;
import br.com.coretech.hero_api.screentime.enums.ScreenStatus;
import br.com.coretech.hero_api.screentime.repositories.ScreenTimeConfigRepository;
import br.com.coretech.hero_api.screentime.repositories.ScreenTimeRequestRepository;
import br.com.coretech.hero_api.tasks.entities.Task;
import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import br.com.coretech.hero_api.tasks.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinorDashboardService {

    private final WalletRepository walletRepository;
    private final TaskRepository taskRepository;
    private final ScreenTimeConfigRepository screenTimeConfigRepository;
    private final ScreenTimeRequestRepository screenTimeRequestRepository;
    private final UserGamificationRepository userGamificationRepository;
    private final DashboardMapper dashboardMapper;

    @Transactional(readOnly = true)
    public MinorDashboardResponseDTO getDashboard(Long minorId) {
        // 1. Dados da Carteira e Cofre
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada para o dependente ID: " + minorId));
        WalletSummaryDTO walletSummary = dashboardMapper.toWalletSummaryDTO(wallet);

        // 2. Intervalo do dia atual
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // 3. Tarefas de Hoje
        List<Task> todayTasks = taskRepository.findTodayTasksByMinorId(
                minorId,
                TaskStatus.PENDING,
                startOfDay,
                endOfDay,
                PageRequest.of(0, 4)
        );
        List<TaskSummaryDTO> tasksSummary = dashboardMapper.toTaskSummaryDTOList(todayTasks);

        // 4. Métricas de Gamificação do Dia (Método Extraído)
        GamificationSummaryDTO gamificationSummary = buildGamificationSummary(minorId, todayTasks.size(), startOfDay, endOfDay);

        // 5. Resumo de Tempo de Tela (Método Extraído)
        ScreenTimeSummaryDTO screenTimeSummary = buildScreenTimeSummary(minorId, startOfDay, endOfDay);

        String dailyReminder = "Solicite seu tempo de tela com sabedoria e use suas fichas com inteligência!";

        return new MinorDashboardResponseDTO(
                walletSummary,
                screenTimeSummary,
                tasksSummary,
                gamificationSummary,
                dailyReminder
        );
    }

    private GamificationSummaryDTO buildGamificationSummary(Long minorId, int totalToday, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        long completedToday = taskRepository.countApprovedTasksToday(
                minorId,
                TaskStatus.APPROVED,
                startOfDay,
                endOfDay
        );

        UserGamification gamification = userGamificationRepository.findByUserId(minorId)
                .orElseGet(() -> {
                    UserGamification initial = new UserGamification();
                    initial.setCurrentLevel(1);
                    initial.setCurrentXp(0);
                    initial.setTargetXp(100);
                    return initial;
                });

        String motivationalMessage = (completedToday > 0 && completedToday == totalToday)
                ? "Parabéns! Você concluiu todas as missões de hoje!"
                : "Você está mandando muito bem nas suas missões!";

        return new GamificationSummaryDTO(
                gamification.getCurrentLevel(),
                gamification.getCurrentXp(),
                gamification.getTargetXp(),
                (int) completedToday,
                totalToday,
                motivationalMessage
        );
    }

    private ScreenTimeSummaryDTO buildScreenTimeSummary(Long minorId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        ScreenTimeConfig config = screenTimeConfigRepository.findByWalletMinorId(minorId).orElse(null);
        int dailyLimit = getLimitForToday(config);

        Integer usedMinutesToday = screenTimeRequestRepository.sumUsedMinutesToday(
                minorId,
                ScreenStatus.APPROVED,
                startOfDay,
                endOfDay
        );
        if (usedMinutesToday == null) {
            usedMinutesToday = 0;
        }

        int remainingMinutesToday = Math.max(0, dailyLimit - usedMinutesToday);
        int tokenToMinutesRatio = (config != null && config.getMinutesPerToken() != null) ? config.getMinutesPerToken() : 30;

        return new ScreenTimeSummaryDTO(
                dailyLimit,
                usedMinutesToday,
                remainingMinutesToday,
                tokenToMinutesRatio
        );
    }

    private int getLimitForToday(ScreenTimeConfig config) {
        if (config == null) return 0;

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        Integer limit = switch (today) {
            case MONDAY -> config.getMondayLimit();
            case TUESDAY -> config.getTuesdayLimit();
            case WEDNESDAY -> config.getWednesdayLimit();
            case THURSDAY -> config.getThursdayLimit();
            case FRIDAY -> config.getFridayLimit();
            case SATURDAY -> config.getSaturdayLimit();
            case SUNDAY -> config.getSundayLimit();
        };

        return limit != null ? limit : 0;
    }
}