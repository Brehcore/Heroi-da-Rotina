package br.com.coretech.hero_api.screentime.services;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import br.com.coretech.hero_api.financial.services.WalletService;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.screentime.dtos.ScreenTimeRequestDTO;
import br.com.coretech.hero_api.screentime.dtos.ScreenTimeResponseDTO;
import br.com.coretech.hero_api.screentime.entities.ScreenTimeConfig;
import br.com.coretech.hero_api.screentime.entities.ScreenTimeRequest;
import br.com.coretech.hero_api.screentime.enums.ScreenStatus;
import br.com.coretech.hero_api.screentime.repositories.ScreenTimeConfigRepository;
import br.com.coretech.hero_api.screentime.repositories.ScreenTimeRequestRepository;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreenTimeService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final ScreenTimeRequestRepository requestRepository;
    private final ScreenTimeConfigRepository configRepository;
    private final HeroMapper heroMapper;

    /**
     * Cria a solicitação após validar o limite do dia da semana e o saldo da carteira.
     */
    @Transactional
    public ScreenTimeResponseDTO requestScreenTime(ScreenTimeRequestDTO requestDTO) {

        // Como o seu DTO é um Java Record, usamos os métodos com o próprio nome do atributo
        Long minorId = requestDTO.minorId();
        Integer minutes = requestDTO.minutes();

        // 1. Busca a configuração de limites do menor
        ScreenTimeConfig config = configRepository.findByWalletMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Configuração de tempo de tela não encontrada. Peça ao monitor para configurar."));

        // 2. Descobre que dia é hoje e puxa o limite correto
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        int dailyLimit = switch (today) {
            case MONDAY -> config.getMondayLimit();
            case TUESDAY -> config.getTuesdayLimit();
            case WEDNESDAY -> config.getWednesdayLimit();
            case THURSDAY -> config.getThursdayLimit();
            case FRIDAY -> config.getFridayLimit();
            case SATURDAY -> config.getSaturdayLimit();
            case SUNDAY -> config.getSundayLimit();
        };

        // 3. Valida se o pedido não fura a regra do monitor para hoje
        if (minutes > dailyLimit) {
            throw new RuntimeException("Bloqueado: O tempo solicitado (" + minutes + " min) excede o limite máximo permitido para hoje (" + dailyLimit + " min).");
        }

        // 4. Busca a carteira para calcular custos e saldo
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        ScreenTimeRequest request = getScreenTimeRequest(minutes, config, wallet);

        // Salva a entidade no banco de dados
        request = requestRepository.save(request);

        // 5. Converte a entidade salva para o DTO de resposta usando o seu Mapper!
        return heroMapper.toScreenTimeResponseDTO(request);
    }

    /**
     * Lista todas as solicitações pendentes para o sininho de notificações do Monitor.
     */
    @Transactional(readOnly = true)
    public List<ScreenTimeResponseDTO> getPendingRequestsForFamily(Long familyId) {
        return requestRepository.findAllByMinorFamiliesIdAndScreenStatus(familyId, ScreenStatus.PENDING)
                .stream()
                .map(heroMapper::toScreenTimeResponseDTO)
                .toList();
    }

    private static @NonNull ScreenTimeRequest getScreenTimeRequest(Integer minutes, ScreenTimeConfig config, Wallet wallet) {
        int cost = (int) Math.ceil((double) minutes / config.getMinutesPerToken());

        if (wallet.getTokenBalances() < cost) {
            throw new RuntimeException("Saldo insuficiente. Você precisa de " + cost + " fichas para " + minutes + " minutos.");
        }

        // 5. Salva a solicitação com status PENDING
        ScreenTimeRequest request = new ScreenTimeRequest();
        request.setMinor(wallet.getMinor());
        request.setRequestedMinutes(minutes);
        request.setTokenCost(cost);
        return request;
    }

    /**
     * Aprova a solicitação e deduz as fichas.
     */
    @Transactional
    public void approveRequest(Long requestId, Long monitorId) {
        ScreenTimeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        if (request.getScreenStatus() != ScreenStatus.PENDING) {
            throw new RuntimeException("Esta solicitação já foi processada.");
        }

        User monitor = userRepository.findById(monitorId)
                .orElseThrow(() -> new RuntimeException("Monitor não encontrado"));

        // Deduz as fichas usando o valor calculado na hora do pedido
        walletService.withdrawTokens(request.getMinor().getId(),
                request.getTokenCost(),
                "Tempo de tela aprovado: " + request.getRequestedMinutes() + "min");

        request.setScreenStatus(ScreenStatus.APPROVED);
        request.setApprovedBy(monitor);
        requestRepository.save(request);
    }

    /**
     * Rejeita a solicitação de tempo de tela (O menor não perde fichas).
     */
    @Transactional
    public void rejectRequest(Long requestId, Long monitorId) {
        ScreenTimeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        if (request.getScreenStatus() != ScreenStatus.PENDING) {
            throw new RuntimeException("Esta solicitação já foi processada.");
        }

        User monitor = userRepository.findById(monitorId)
                .orElseThrow(() -> new RuntimeException("Monitor não encontrado"));

        request.setScreenStatus(ScreenStatus.REJECTED);
        request.setApprovedBy(monitor);
        requestRepository.save(request);
    }
}