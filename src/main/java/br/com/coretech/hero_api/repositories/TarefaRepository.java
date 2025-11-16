package br.com.coretech.hero_api.repositories;

import br.com.coretech.hero_api.entities.Tarefa;
import br.com.coretech.hero_api.enums.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    /**
     * Para o app do Menor: Lista todas as tarefas dele.
     */
    List<Tarefa> findAllByMenorId(Long menorId);

    /**
     * Para o app do Menor: Filtra as tarefas por status (ex: "PENDENTE").
     */
    List<Tarefa> findAllByMenorIdAndStatus(Long menorId, StatusTarefa status);

    /**
     * Para o app do Monitor: Mostra tarefas que precisam de aprovação.
     * Busca tarefas CONCLUIDAS de todos os menores da sua família.
     */
    List<Tarefa> findAllByMenorFamiliaIdAndStatus(Long familiaId, StatusTarefa status);
}
