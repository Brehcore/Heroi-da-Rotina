package br.com.coretech.hero_api.users.services;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.users.dtos.ResetPasswordDTO;
import br.com.coretech.hero_api.users.dtos.UserRegisterDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.users.entities.PasswordResetToken;
import br.com.coretech.hero_api.users.enums.UserRole;
import br.com.coretech.hero_api.users.dtos.UserCreateDTO;
import br.com.coretech.hero_api.users.entities.Family;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import br.com.coretech.hero_api.users.repositories.FamilyRepository;
import br.com.coretech.hero_api.users.repositories.PasswordResetTokenRepository;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final WalletRepository walletRepository;
    private final HeroMapper heroMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

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

    @Transactional
    public UserResponseDTO createUser(UserCreateDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        user.setProfilePictureUrl(resolveProfilePictureUrl(dto.getProfilePictureUrl(), user.getName()));

        // 1. Busca família APENAS se o ID foi enviado
        if (dto.getFamilyId() != null) {
            Family family = familyRepository.findById(dto.getFamilyId())
                    .orElseThrow(() -> new RuntimeException("Família não encontrada com ID: " + dto.getFamilyId()));

            user.setFamilies(new HashSet<>(List.of(family)));
        } else {
            user.setFamilies(new HashSet<>());
        }

        // 2. Salva o Usuário
        user = userRepository.save(user);

        // 3. Se for MENOR, cria a Wallet automaticamente
        if (dto.getRole() == UserRole.MINOR) {
            Wallet wallet = new Wallet();
            wallet.setMinor(user);
            wallet.setTokenBalances(0);
            wallet.setMoneyBalances(0.0);
            wallet.setHistoricalTokens(new ArrayList<>());
            wallet.setHistoricalMoney(new ArrayList<>());

            walletRepository.save(wallet);
        }

        return heroMapper.toUserDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário com ID: " + id + " não encontrado.");
        }
        userRepository.deleteById(id);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return;
        }

        String token = java.util.UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();

        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setTokenExpiration(LocalDateTime.now().plusMinutes(10));

        passwordResetTokenRepository.save(passwordResetToken);
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

    @Transactional(readOnly = true)
    public UserResponseDTO getAuthenticatedUserDTO(String email) {
        // Busca o usuário com a foto e as famílias carregadas
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        UserResponseDTO dto = heroMapper.toUserDTO(user);

        // Se o usuário possuir famílias, vinculamos a primeira ao DTO para exibição
        if (user.getFamilies() != null && !user.getFamilies().isEmpty()) {
            Family primaryFamily = user.getFamilies().iterator().next();
            dto.setFamilyId(primaryFamily.getId());
            dto.setFamilyName(primaryFamily.getFamilyName());
        }

        return dto;
    }

    private String resolveProfilePictureUrl(String providedUrl, String userName) {
        if (providedUrl != null && !providedUrl.isBlank()) {
            return providedUrl;
        }
        String nomeLimpo = userName.replaceAll("\\s+", "");
        return "https://api.dicebear.com/8.x/bottts/svg?seed=" + nomeLimpo;
    }
}