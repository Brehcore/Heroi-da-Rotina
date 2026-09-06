package br.com.coretech.hero_api.dashboard.mappers;

import br.com.coretech.hero_api.dashboard.dtos.TaskSummaryDTO;
import br.com.coretech.hero_api.dashboard.dtos.WalletSummaryDTO;
import br.com.coretech.hero_api.tasks.entities.Task;
import br.com.coretech.hero_api.financial.entities.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DashboardMapper {

    // Mapeia os dados da carteira diretamente para o resumo
    WalletSummaryDTO toWalletSummaryDTO(Wallet wallet);

    // Mapeia uma tarefa individual
    @SuppressWarnings("unused")
    TaskSummaryDTO toTaskSummaryDTO(Task task);

    // Mapeia a lista de tarefas do dia
    List<TaskSummaryDTO> toTaskSummaryDTOList(List<Task> tasks);
}