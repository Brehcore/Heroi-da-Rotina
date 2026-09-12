package br.com.coretech.hero_api.financial.piggybank.service;

import br.com.coretech.hero_api.exceptions.ResourceNotFoundException;
import br.com.coretech.hero_api.financial.wallet.entity.MoneyTransaction;
import br.com.coretech.hero_api.financial.piggybank.entity.SavingsGoal;
import br.com.coretech.hero_api.financial.piggybank.dto.PiggyBankDashboardDTO;
import br.com.coretech.hero_api.financial.piggybank.dto.SavingsGoalCreateDTO;
import br.com.coretech.hero_api.financial.piggybank.dto.SavingsGoalDepositDTO;
import br.com.coretech.hero_api.financial.piggybank.dto.SavingsGoalResponseDTO;
import br.com.coretech.hero_api.financial.piggybank.dto.SavingsGoalWithdrawDTO;
import br.com.coretech.hero_api.financial.wallet.entity.Wallet;
import br.com.coretech.hero_api.financial.piggybank.enums.SavingsGoalStatus;
import br.com.coretech.hero_api.financial.transaction.enums.TransactionType;
import br.com.coretech.hero_api.financial.piggybank.mappers.SavingsGoalMapper;
import br.com.coretech.hero_api.financial.piggybank.repository.SavingsGoalRepository;
import br.com.coretech.hero_api.financial.wallet.repository.WalletRepository;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final SavingsGoalMapper savingsGoalMapper;

    @Transactional(readOnly = true)
    public PiggyBankDashboardDTO getPiggyBankDashboard(Long minorId) {
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada para o dependente ID: " + minorId));

        BigDecimal liquidBalance = BigDecimal.valueOf(wallet.getMoneyBalances() != null ? wallet.getMoneyBalances() : 0.0);
        BigDecimal totalSavedInGoals = savingsGoalRepository.sumSavedAmountByMinorId(minorId);
        if (totalSavedInGoals == null) {
            totalSavedInGoals = BigDecimal.ZERO;
        }

        BigDecimal totalPiggy = liquidBalance.add(totalSavedInGoals);

        boolean interestEnabled = Boolean.TRUE.equals(wallet.getInterestEnabled());
        double interestRate = wallet.getInterestRate() != null ? wallet.getInterestRate() : 0.0;
        String frequency = wallet.getInterestFrequency() != null ? wallet.getInterestFrequency().name() : "WEEKLY";

        // Projeção do rendimento no próximo ciclo sobre o valor total sob custódia
        BigDecimal projectedYield = BigDecimal.ZERO;
        if (interestEnabled && interestRate > 0) {
            BigDecimal rateMultiplier = BigDecimal.valueOf(interestRate / 100.0);
            projectedYield = totalPiggy.multiply(rateMultiplier).setScale(2, RoundingMode.HALF_UP);
        }

        String motivation = interestEnabled
                ? String.format("Seu cofrinho rende %s%% por ciclo! Deixando seu saldo guardado, você pode render +R$ %s.",
                interestRate, projectedYield)
                : "Peça ao seu monitor para ativar o rendimento automático e ver o seu cofrinho crescer!";

        List<SavingsGoal> goalEntities = savingsGoalRepository.findAllByMinorIdOrderByCreatedAtDesc(minorId);
        List<SavingsGoalResponseDTO> goals = savingsGoalMapper.toDTOList(goalEntities);

        return new PiggyBankDashboardDTO(
                liquidBalance.setScale(2, RoundingMode.HALF_UP),
                totalSavedInGoals.setScale(2, RoundingMode.HALF_UP),
                totalPiggy.setScale(2, RoundingMode.HALF_UP),
                interestEnabled,
                interestRate,
                frequency,
                projectedYield,
                motivation,
                goals
        );
    }

    @Transactional
    public SavingsGoalResponseDTO createGoal(Long minorId, SavingsGoalCreateDTO dto) {
        User minor = userRepository.findById(minorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário menor não encontrado com ID: " + minorId));

        SavingsGoal goal = new SavingsGoal();
        goal.setTitle(dto.title());
        goal.setDescription(dto.description());
        goal.setTargetAmount(dto.targetAmount());
        goal.setTargetDate(dto.targetDate());
        goal.setIcon((dto.icon() != null && !dto.icon().isBlank()) ? dto.icon() : "TARGET");
        goal.setMinor(minor);
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setStatus(SavingsGoalStatus.IN_PROGRESS);

        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        return savingsGoalMapper.toDTO(savedGoal);
    }

    /**
     * Aloca saldo líquido disponível na carteira diretamente para uma meta específica.
     */
    @Transactional
    public SavingsGoalResponseDTO depositToGoal(Long minorId, Long goalId, SavingsGoalDepositDTO dto) {
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada para o dependente ID: " + minorId));

        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta de poupança não encontrada com ID: " + goalId));

        if (!goal.getMinor().getId().equals(minorId)) {
            throw new IllegalArgumentException("Esta meta de poupança não pertence a este menor.");
        }

        if (goal.getStatus() != SavingsGoalStatus.IN_PROGRESS) {
            throw new IllegalStateException("Não é possível depositar em uma meta finalizada ou cancelada.");
        }

        BigDecimal depositAmount = dto.amount();
        BigDecimal currentLiquid = BigDecimal.valueOf(wallet.getMoneyBalances() != null ? wallet.getMoneyBalances() : 0.0);

        if (depositAmount.compareTo(currentLiquid) > 0) {
            throw new IllegalArgumentException(String.format(
                    "Saldo insuficiente na carteira. Saldo disponível: R$ %.2f, tentativa de depósito: R$ %.2f.",
                    currentLiquid, depositAmount
            ));
        }

        // 1. Debita da carteira líquida
        wallet.setMoneyBalances(currentLiquid.subtract(depositAmount).doubleValue());

        MoneyTransaction tx = new MoneyTransaction();
        tx.setWallet(wallet);
        tx.setType(TransactionType.DEBIT);
        tx.setValue(depositAmount.doubleValue());
        tx.setMotive("Depósito na meta: " + goal.getTitle());
        tx.setDate(LocalDateTime.now());
        wallet.getHistoricalMoney().add(tx);
        walletRepository.save(wallet);

        // 2. Credita o valor na meta de poupança
        BigDecimal newGoalAmount = goal.getCurrentAmount().add(depositAmount);
        goal.setCurrentAmount(newGoalAmount);

        // Verifica se a meta foi alcançada
        if (newGoalAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(SavingsGoalStatus.COMPLETED);
            goal.setCompletedAt(LocalDateTime.now());
        }

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        return savingsGoalMapper.toDTO(updatedGoal);
    }

    /**
     * Resgata o saldo alocado na meta de volta para o saldo líquido da carteira ("Quebrar Cofrinho").
     */
    @Transactional
    public SavingsGoalResponseDTO withdrawFromGoal(Long minorId, Long goalId, SavingsGoalWithdrawDTO dto) {
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada para o dependente ID: " + minorId));

        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta de poupança não encontrada com ID: " + goalId));

        if (!goal.getMinor().getId().equals(minorId)) {
            throw new IllegalArgumentException("Esta meta de poupança não pertence a este menor.");
        }

        BigDecimal withdrawAmount = dto.amount();
        if (withdrawAmount.compareTo(goal.getCurrentAmount()) > 0) {
            throw new IllegalArgumentException(String.format(
                    "Valor de resgate excede o saldo da meta. Saldo guardado: R$ %.2f, tentativa de resgate: R$ %.2f.",
                    goal.getCurrentAmount(), withdrawAmount
            ));
        }

        // 1. Debita da meta
        BigDecimal updatedGoalBalance = goal.getCurrentAmount().subtract(withdrawAmount);
        goal.setCurrentAmount(updatedGoalBalance);

        // Se estava como concluída mas foi resgatada abaixo do target, volta para em andamento
        if (goal.getStatus() == SavingsGoalStatus.COMPLETED && updatedGoalBalance.compareTo(goal.getTargetAmount()) < 0) {
            goal.setStatus(SavingsGoalStatus.IN_PROGRESS);
            goal.setCompletedAt(null);
        }
        savingsGoalRepository.save(goal);

        // 2. Credita o saldo na carteira líquida
        BigDecimal currentLiquid = BigDecimal.valueOf(wallet.getMoneyBalances() != null ? wallet.getMoneyBalances() : 0.0);
        wallet.setMoneyBalances(currentLiquid.add(withdrawAmount).doubleValue());

        MoneyTransaction tx = new MoneyTransaction();
        tx.setWallet(wallet);
        tx.setType(TransactionType.CREDIT);
        tx.setValue(withdrawAmount.doubleValue());
        tx.setMotive("Resgate da meta: " + goal.getTitle());
        tx.setDate(LocalDateTime.now());
        wallet.getHistoricalMoney().add(tx);
        walletRepository.save(wallet);

        return savingsGoalMapper.toDTO(goal);
    }
}