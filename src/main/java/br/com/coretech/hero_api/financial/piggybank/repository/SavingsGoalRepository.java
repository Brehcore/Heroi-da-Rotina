package br.com.coretech.hero_api.financial.piggybank.repository;

import br.com.coretech.hero_api.financial.piggybank.entity.SavingsGoal;
import br.com.coretech.hero_api.financial.piggybank.enums.SavingsGoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    List<SavingsGoal> findAllByMinorIdOrderByCreatedAtDesc(Long minorId);

    List<SavingsGoal> findAllByMinorIdAndStatus(Long minorId, SavingsGoalStatus status);

    @Query("SELECT COALESCE(SUM(g.currentAmount), 0) FROM SavingsGoal g WHERE g.minor.id = :minorId AND g.status = 'IN_PROGRESS'")
    BigDecimal sumSavedAmountByMinorId(@Param("minorId") Long minorId);
}