package br.com.coretech.hero_api.auth.services;

import br.com.coretech.hero_api.auth.dtos.ChangePasswordDTO;
import br.com.coretech.hero_api.auth.dtos.LoginRequestDTO;
import br.com.coretech.hero_api.auth.dtos.LoginResponseDTO;
import br.com.coretech.hero_api.auth.dtos.ResetPasswordDTO;
import br.com.coretech.hero_api.auth.repositories.PasswordResetTokenRepository;
import br.com.coretech.hero_api.jwt.services.JwtService;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.users.dtos.UserRegisterDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.auth.entities.PasswordResetToken;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.enums.UserRole;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import br.com.coretech.hero_api.utils.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailNotificationService emailService;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final HeroMapper heroMapper;

    @Transactional
    public UserResponseDTO registerUser(UserRegisterDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.MONITOR);

        user.setProfilePictureUrl(resolveProfilePictureUrl(dto.getProfilePictureUrl(), user.getName()));

        user = userRepository.save(user);
        return heroMapper.toUserDTO(user);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        // 1. O Spring Security verifica se o e-mail e senha estão corretos
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Se a senha está correta, buscamos o usuário completo no banco
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 3. Geramos o Token JWT
        String jwtToken = jwtService.generateToken(user);

        // 4. Retornamos o DTO completo com o Token e as informações do usuário!
        return new LoginResponseDTO(
                jwtToken,
                user.getId(),
                user.getName(),
                user.getRole() // A ROLE VAI AQUI!
        );
    }

    private String resolveProfilePictureUrl(String providedUrl, String userName) {
        if (providedUrl != null && !providedUrl.isBlank()) {
            return providedUrl;
        }
        String nomeLimpo = userName.replaceAll("\\s+", "");
        return "https://api.dicebear.com/8.x/bottts/svg?seed=" + nomeLimpo;
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return;
        }

        String token = java.util.UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user)
                .orElse(new PasswordResetToken());

        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setTokenExpiration(LocalDateTime.now().plusMinutes(10));

        passwordResetTokenRepository.save(passwordResetToken);

        String resetLink = frontendUrl + "/redefinir-senha?token=" + token;

        String assunto = "Redefinição de Senha";
        String corpo = "Olá, " + user.getName() + "!\n\n" +
                "Recebemos um pedido para redefinir a sua senha. Clique no link abaixo para criar uma nova (válido por 10 minutos):\n" +
                resetLink + "\n\n" +
                "Se não foi você que solicitou, ignore este e-mail.";

        emailService.sendEmail(user.getEmail(), assunto, corpo);
    }

    public void resetPasswordWithToken(ResetPasswordDTO dto) {

        PasswordResetToken tokenEntity = passwordResetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado."));

        if (tokenEntity.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado. Solicite um novo link.");
        }

        User user = tokenEntity.getUser();
        String hashedPassword = passwordEncoder.encode(dto.getNewPassword());

        user.setPassword(hashedPassword);
        userRepository.save(user);

        passwordResetTokenRepository.delete(tokenEntity);
    }

    @Transactional
    public void changePassword(ChangePasswordDTO dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("A senha atual está incorreta.");
        }
        if (dto.getNewPassword().equals(dto.getOldPassword())) {
            throw new RuntimeException("A nova senha deve ser diferente da senha atual.");
        }
        String newCryptedPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(newCryptedPassword);
        userRepository.save(user);
    }
}