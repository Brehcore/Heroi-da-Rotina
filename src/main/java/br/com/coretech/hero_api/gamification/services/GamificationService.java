package br.com.coretech.hero_api.gamification.services;

import br.com.coretech.hero_api.gamification.entities.UserGamification;
import br.com.coretech.hero_api.gamification.repositories.UserGamificationRepository;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamificationService {

    private final UserGamificationRepository userGamificationRepository;
    private final UserRepository userRepository;

    /**
     * Concede XP ao menor com base nas fichas da tarefa aprovada.
     * Fórmula: Se tiver fichas: tokens * 20 XP. Se for tarefa sem fichas (0): 15 XP base.
     */
    @Transactional
    public void grantXpForApprovedTask(Long minorId, Integer tokenReward) {
        int xpEarned = calculateXp(tokenReward);

        UserGamification gamification = userGamificationRepository.findByUserId(minorId)
                .orElseGet(() -> createInitialGamification(minorId));

        int newCurrentXp = gamification.getCurrentXp() + xpEarned;
        int currentLevel = gamification.getCurrentLevel();
        int targetXp = gamification.getTargetXp();

        // Verifica se subiu de nível (suporta múltiplos level-ups se ganhar muito XP de uma vez)
        while (newCurrentXp >= targetXp) {
            newCurrentXp -= targetXp;
            currentLevel++;
            targetXp = calculateNextTargetXp(currentLevel);
            log.info("🎉 Menor ID {} SUBIU DE NÍVEL! Novo Nível: {} | Próxima Meta: {} XP", minorId, currentLevel, targetXp);
        }

        gamification.setCurrentXp(newCurrentXp);
        gamification.setCurrentLevel(currentLevel);
        gamification.setTargetXp(targetXp);

        userGamificationRepository.save(gamification);
        log.info("⭐ Menor ID {} ganhou {} XP. Progresso atual: {}/{} XP (Nível {})",
                minorId, xpEarned, newCurrentXp, targetXp, currentLevel);
    }

    private int calculateXp(Integer tokenReward) {
        if (tokenReward == null || tokenReward <= 0) {
            return 15; // XP base para incentivar hábitos que não remuneram em dinheiro/fichas
        }
        return tokenReward * 20; // 1 ficha = 20 XP, 2 fichas = 40 XP, etc.
    }

    private int calculateNextTargetXp(int nextLevel) {
        // Curva suave: Nível 1 = 100, Nível 2 = 200, Nível 3 = 350, Nível 4 = 550 (+ level * 50)
        return 100 + (nextLevel * 50);
    }

    private UserGamification createInitialGamification(Long minorId) {
        User user = userRepository.findById(minorId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para gamificação: " + minorId));

        UserGamification initial = new UserGamification();
        initial.setUser(user);
        initial.setCurrentLevel(1);
        initial.setCurrentXp(0);
        initial.setTargetXp(100);
        return userGamificationRepository.save(initial);
    }
}