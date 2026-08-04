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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Value("${app.backend.url}")
    private String backendUrl;

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

        // 6. Prepara a entidade com os minutos calculados e GERA O TOKEN
        ScreenTimeRequest request = getScreenTimeRequest(minutesRequested, config, wallet);

        // Geração do token único de aprovação
        String tokenDeAprovacao = java.util.UUID.randomUUID().toString();
        request.setApprovalToken(tokenDeAprovacao);

        request = requestRepository.save(request);

        // Como o modelo mapeia que um usuário pode ter várias famílias, pegamos a primeira cadastrada do menor
        Long familyId = wallet.getMinor().getFamilies().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Menor não está vinculado a nenhuma família para receber notificações."))
                .getId();

        ScreenTimeResponseDTO dto = heroMapper.toScreenTimeResponseDTO(request);
        String destino = "/topic/notifications/family/" + familyId;
        simpMessagingTemplate.convertAndSend(destino, dto);

        // Busca todos os usuários com papel MONITOR que pertencem a esta mesma família
        List<User> monitoresDaFamilia = userRepository.findByFamilies_IdAndRole(familyId, UserRole.MONITOR);
        String nomeDoMenor = wallet.getMinor().getName();
        String assunto = "🎮 Pedido de Tempo de Tela: " + nomeDoMenor;

        // Dispara o e-mail customizado para CADA monitor, colocando o ID dele no link
        for (User monitor : monitoresDaFamilia) {

            // Montando os links de ação
            String linkAprovar = String.format("%s/api/screentime/request/email-action?token=%s&action=APPROVE&monitorId=%d", backendUrl, tokenDeAprovacao, monitor.getId());
            String linkRejeitar = String.format("%s/api/screentime/request/email-action?token=%s&action=REJECT&monitorId=%d", backendUrl, tokenDeAprovacao, monitor.getId());

            String corpoHtml = String.format("""
                <div style="font-family: Arial, sans-serif; background-color: #f4f7f6; padding: 20px;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
                        <div style="background-color: #4A90E2; padding: 20px; text-align: center; color: white;">
                            <h2 style="margin: 0;">🎮 Tempo de Tela Solicitado!</h2>
                        </div>
                        <div style="padding: 30px; color: #333333; line-height: 1.6;">
                            <p>Olá, %s!</p>
                            <p>O herói <strong>%s</strong> acabou de trocar fichas por minutos de diversão.</p>
                            <div style="background-color: #f9f9f9; border-left: 4px solid #4A90E2; padding: 15px; margin: 20px 0;">
                                <p style="margin: 0; font-size: 16px;">🪙 <strong>Fichas gastas:</strong> %d</p>
                                <p style="margin: 10px 0 0 0; font-size: 16px;">⏱️ <strong>Tempo resgatado:</strong> %d minutos</p>
                            </div>
                            <div style="text-align: center; margin-top: 30px;">
                                <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; margin-right: 10px; display: inline-block;">✅ Aprovar</a>
                                <a href="%s" style="background-color: #F44336; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">❌ Rejeitar</a>
                            </div>
                        </div>
                    </div>
                </div>
                """, monitor.getName(), nomeDoMenor, tokensToSpend, minutesRequested, linkAprovar, linkRejeitar);

            emailService.sendEmail(monitor.getEmail(), assunto, corpoHtml);
        }

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

        // Deduz as fichas da carteira do menor
        walletService.withdrawTokens(request.getMinor().getId(),
                request.getTokenCost(),
                "Tempo de tela aprovado: " + request.getRequestedMinutes() + "min");

        request.setScreenStatus(ScreenStatus.APPROVED);
        request.setApprovedBy(monitor);
        requestRepository.save(request);

        ScreenTimeResponseDTO dto = heroMapper.toScreenTimeResponseDTO(request);

        // Resgata o ID da família
        Long familyId = request.getMinor().getFamilies().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Menor não está vinculado a nenhuma família."))
                .getId();

        // 1º ENVIO: Para o Menor (Avisa que foi aprovado)
        String destinoMenor = "/topic/notifications/minor/" + request.getMinor().getId();
        simpMessagingTemplate.convertAndSend(destinoMenor, dto);

        // 2º ENVIO: Para a Família (Avisa os monitores para removerem o card do sininho)
        String destinoFamilia = "/topic/notifications/family/" + familyId;
        simpMessagingTemplate.convertAndSend(destinoFamilia, dto);
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

        ScreenTimeResponseDTO dto = heroMapper.toScreenTimeResponseDTO(request);

        Long familyId = request.getMinor().getFamilies().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Menor não está vinculado a nenhuma família."))
                .getId();

        // 1º ENVIO: Para o Menor (Avisa que foi rejeitado)
        String destinoMenor = "/topic/notifications/minor/" + request.getMinor().getId();
        simpMessagingTemplate.convertAndSend(destinoMenor, dto);

        // 2º ENVIO: Para a Família (Avisa os monitores para removerem o card do sininho)
        String destinoFamilia = "/topic/notifications/family/" + familyId;
        simpMessagingTemplate.convertAndSend(destinoFamilia, dto);
    }

    /**
     * Processa a ação vinda diretamente do clique do e-mail.
     * Retorna uma String com uma mensagem amigável em HTML para exibir no navegador.
     */
    @Transactional
    public String processEmailAction(String token, String action, Long monitorId) {
        ScreenTimeRequest request = requestRepository.findByApprovalToken(token)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada ou link inválido."));

        if (request.getScreenStatus() != ScreenStatus.PENDING) {
            return "<h3>⚠️ Esta solicitação já foi respondida anteriormente.</h3>";
        }

        try {
            if ("APPROVE".equalsIgnoreCase(action)) {
                approveRequest(request.getId(), monitorId);
                return "<h3 style='color: green;'>✅ Sucesso! O tempo de tela foi APROVADO e o herói já foi notificado.</h3>";
            } else if ("REJECT".equalsIgnoreCase(action)) {
                rejectRequest(request.getId(), monitorId);
                return "<h3 style='color: red;'>❌ Solicitação REJEITADA. Nenhuma ficha foi descontada.</h3>";
            } else {
                return "<h3>Ação inválida.</h3>";
            }
        } catch (Exception e) {
            return "<h3>Erro ao processar: " + e.getMessage() + "</h3>";
        }
    }
}