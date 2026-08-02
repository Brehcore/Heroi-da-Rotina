package br.com.coretech.hero_api.config.security.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. Define a porta de entrada (Handshake)
        registry.addEndpoint("/ws-hero")
                .setAllowedOriginPatterns("*") // Permite o Angular conectar de qualquer IP/Porta
                .withSockJS(); // Ativa um "plano B" caso o navegador do usuário bloqueie WebSockets puros
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 2. Define o prefixo das mensagens que saem do Servidor para o Front-end
        registry.enableSimpleBroker("/topic");

        // 3. Define o prefixo das mensagens que saem do Front-end para o Servidor (opcional por enquanto)
        registry.setApplicationDestinationPrefixes("/app");
    }
}
