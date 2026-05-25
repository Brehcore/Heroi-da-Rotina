package br.com.coretech.hero_api.utils.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(String to, String subject, String text) {
        try {
            // 1. Cria uma mensagem avançada que suporta HTML
            MimeMessage message = mailSender.createMimeMessage();

            // 2. O 'true' habilita o modo multipart e o 'UTF-8' garante que os acentos/emojis funcionem
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 3. Remetente: Além do e-mail, agora você pode colocar um Nome Bonito!
            helper.setFrom("brehcore@gmail.com", "Herói da Rotina");

            // O .trim() evita erros caso o e-mail venha com espaço no final
            helper.setTo(to.trim());

            helper.setSubject(subject);

            // 4. A MÁGICA: Esse 'true' no final avisa o Gmail que o texto é um código HTML
            helper.setText(text, true);

            mailSender.send(message);
            System.out.println("E-mail enviado com sucesso para: " + to);

        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail para " + to + ": " + e.getMessage());
            e.printStackTrace(); // Imprime o erro completo no console para facilitar a investigação se algo der errado
        }
    }
}
