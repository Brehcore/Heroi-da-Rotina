package br.com.coretech.hero_api.services;

import br.com.coretech.hero_api.dtos.TarefaCreateDTO;
import br.com.coretech.hero_api.dtos.TarefaResponseDTO;
import br.com.coretech.hero_api.entities.Tarefa;
import br.com.coretech.hero_api.entities.Usuario;
import br.com.coretech.hero_api.enums.StatusTarefa;
import br.com.coretech.hero_api.repositories.TarefaRepository;
import br.com.coretech.hero_api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarteiraService carteiraService; // Injeção do serviço financeiro

    /**
     * Cria uma nova tarefa atribuída a um menor.
     */
    @Transactional
    public TarefaResponseDTO criarTarefa(TarefaCreateDTO dto) {
        // 1. Validar se o menor existe
        Usuario menor = usuarioRepository.findById(dto.getMenorId())
                .orElseThrow(() -> new RuntimeException("Menor não encontrado com ID: " + dto.getMenorId()));

        // 2. Buscar o monitor (opcional, pode ser null se criado pelo sistema ou sem login)
        Usuario monitor = null;
        if (dto.getMonitorCriadorId() != null) {
            monitor = usuarioRepository.findById(dto.getMonitorCriadorId())
                    .orElse(null);
        }

        // 3. Montar a entidade
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setRecompensaFichas(dto.getRecompensaFichas());
        tarefa.setMenor(menor);
        tarefa.setMonitorCriador(monitor);
        tarefa.setStatus(StatusTarefa.PENDENTE); // Status inicial padrão

        // 4. Salvar no banco
        tarefa = tarefaRepository.save(tarefa);

        // 5. Retornar DTO
        return TarefaResponseDTO.fromEntity(tarefa);
    }

    /**
     * Ação do MENOR: Marca a tarefa como feita.
     * Não gera pagamento ainda, apenas sinaliza para o monitor.
     */
    @Transactional
    public void concluirTarefa(Long tarefaId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElse