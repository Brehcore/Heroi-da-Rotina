package br.com.coretech.hero_api.screentime.services;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import br.com.coretech.hero_api.financial.services.WalletService;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.screentime.dtos.ScreenTimeResponseDTO;
import br.com.coretech.hero_api.screentime.dtos.TokenExchangeRequestDTO;
import br.com.coretech.hero_api.screentime.entities.ScreenTimeConfig;
import br.com.coretech.hero_api.screentime.entities.ScreenTimeRequest;
import br.com.coretech.hero_api.screentime.enums.ScreenStatus;
import br.com.coretech.hero_api.screentime.repositories.ScreenTimeConfigRepository;
import br.com.coretech.hero_api.screentime.repositories.ScreenTimeRequestRepository;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.enums.UserRole;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import br.com.coretech.hero_api.utils.service.EmailNotificationService;
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
    private final EmailNotificationService emailService;

    /**
     * Cria a solicitação após converter fichas em tempo, validar o limite do dia e o saldo da carteira.
     */
    @Transactional
    public ScreenTimeResponseDTO exchangeTokensForTime(TokenExchangeRequestDTO requestDTO) {

        Long minorId = requestDTO.minorId();
        Integer tokensToSpend = requestDTO.tokens();

        // 1. Busca a configuração de limites do menor
        ScreenTimeConfig config = configRepository.findByWalletMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Configuração de tempo de tela não encontrada. Peça ao monitor para configurar."));

        // 2. Calcula os minutos reais baseados na regra definida pelo monitor (ex: 1 ficha = 30 min)
        int minutesRequested = tokensToSpend * config.getMinutesPerToken();

        // 3. Descobre que dia é hoje e puxa o limite correto
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

        // 4. Valida se o tempo calculado não fura a regra do monitor para hoje
        if (minutesRequested > dailyLimit) {
            throw new RuntimeException(String.format("Bloqueado: A troca de %d fichas gera %d minutos, o que excede seu limite diário restante de %d min.",
                    tokensToSpend, minutesRequested, dailyLimit));
        }

        // 5. Busca a carteira para validar saldo de fichas
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        // CORREÇÃO AQUI: Trocando getBalance() por getTokenBalances()
        if (wallet.getTokenBalances() < tokensToSpend) {
            throw new RuntimeException("Saldo insuficiente. Você possui apenas " + wallet.getTokenBalances() + " fichas.");
        }

        // Obs: Se a dedução do saldo não estiver sendo feita dentro de getScreenTimeRequest(),
        // faça a dedução e salve a carteira aqui:
        // wallet.setBalance(wallet.getBalance() - tokensToSpend);
        // walletRepository.save(wallet);

        // 6. Prepara a entidade com os minutos calculados
        ScreenTimeRequest request = getScreenTimeRequest(minutesRequested, config, wallet);

        // Salva a solicitação no banco de dados
        request = requestRepository.save(request);

        // Como o modelo mapeia que um usuário pode ter várias famílias, pegamos a primeira cadastrada do menor
        Long familyId = wallet.getMinor().getFamilies().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Menor não está vinculado a nenhuma família para receber notificações."))
                .getId();

        // Busca todos os usuários com papel MONITOR que pertencem a esta mesma família
        List<User> monitoresDaFamilia = userRepository.findByFamilies_IdAndRole(familyId, UserRole.MONITOR);

        String nomeDoMenor = wallet.getMinor().getName();
        String assunto = "🎮 Pedido de Tempo de Tela: " + nomeDoMenor;
        String corpoHtml = String.format("""
            <div style="font-family: Arial, sans-serif; background-color: #f4f7f6; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
                    <div style="background-color: #4A90E2; padding: 20px; text-align: center; color: white;">
                        <h2 style="margin: 0;">🎮 Tempo de Tela Solicitado!</h2>
                    </div>
                    <div style="padding: 30px; color: #333333; line-height: 1.6;">
                        <p>Olá, Monitor!</p>
                        <p>O herói <strong>%s</strong> acabou de trocar fichas por minutos de diversão.</p>
                        <div style="background-color: #f9f9f9; border-left: 4px solid #4A90E2; padding: 15px; margin: 20px 0;">
                            <p style="margin: 0; font-size: 16px;">🪙 <strong>Fichas gastas:</strong> %d</p>
                            <p style="margin: 10px 0 0 0; font-size: 16px;">⏱️ <strong>Tempo resgatado:</strong> %d minutos</p>
                        </div>
                        <p style="font-size: 14px; color: #777;">Acompanhe o painel do aplicativo para gerenciar os limites e o histórico.</p>
                    </div>
                </div>
            </div>
            """, nomeDoMenor, tokensToSpend, minutesRequested);

        // Dispara o e-mail de forma assíncrona para cada monitor encontrado
        for (User monitor : monitoresDaFamilia) {
            emailService.sendEmail(monitor.getEmail(), assunto, corpoHtml);
        }

        // 7. Converte e retorna
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