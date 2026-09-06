package br.com.coretech.hero_api.tasks.repositories;

import br.com.coretech.hero_api.tasks.entities.Task;
import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Para o app do Menor: Lista todas as tarefas dele.
     */
    List<Task> findAllByMinorId(Long minorId);

    /**
     * Para o app do Menor: Filtra as tarefas por status (ex: "PENDENTE").
     */
    List<Task> findAllByMinorIdAndStatus(Long minorId, TaskStatus status);

    /**
     * Para o app do Monitor: Mostra tarefas que precisam de aprovação.
     * Busca tarefas CONCLUIDAS de todos os menores da sua família.
     */
    List<Task> findAllByMinorFamiliesIdAndStatus(Long familiesId, TaskStatus status);

    // O Spring monta o SQL com LIMIT, OFFSET e o WHERE correto automaticamente!
    Page<Task> findByMinor_Families_Id(Long familyId, Pageable pageable);

    void deleteByMinorId(Long minorId);

    // Busca tarefas do menor criadas hoje ou ainda pendentes para realizar hoje
    @Query("""
        SELECT t FROM Task t 
        WHERE t.minor.id = :minorId 
          AND (
            (t.creationDate >= :startOfDay AND t.creationDate <= :endOfDay)
            OR t.status = :pendingStatus
          )
        ORDER BY t.id DESC
    """)
    List<Task> findTodayTasksByMinorId(
            @Param("minorId") Long minorId,
            @Param("pendingStatus") TaskStatus pendingStatus,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable
    );

    // Contagem de tarefas aprovadas no hoje (para a gamificação do dashboard)
    @Query("""
        SELECT COUNT(t) FROM Task t 
        WHERE t.minor.id = :minorId 
          AND t.status = :approvedStatus
          AND t.approvalDate >= :startOfDay 
          AND t.approvalDate <= :endOfDay
    """)
    long countApprovedTasksToday(
            @Param("minorId") Long minorId,
            @Param("approvedStatus") TaskStatus approvedStatus,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
