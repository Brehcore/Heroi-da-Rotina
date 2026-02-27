package br.com.coretech.hero_api.tasks.repositories;

import br.com.coretech.hero_api.tasks.entities.Task;
import br.com.coretech.hero_api.tasks.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
    List<Task> findAllByMinorFamilyIdAndStatus(Long familyId, TaskStatus status);
}
