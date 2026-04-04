package br.com.coretech.hero_api.users.services;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.users.enums.UserRole;
import br.com.coretech.hero_api.users.dtos.UserCreateDTO;
import br.com.coretech.hero_api.users.entities.Family;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import br.com.coretech.hero_api.users.repositories.FamilyRepository;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public UserResponseDTO createUser(UserCreateDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        // Criptografando a senha obrigatoriamente para o Spring Security funcionar depois
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        // 1. Busca família APENAS se o ID foi enviado
        if (dto.getFamilyId() != null) {
            Family family = familyRepository.findById(dto.getFamilyId())
                    .orElseThrow(() -> new RuntimeException("Família não encontrada com ID: " + dto.getFamilyId()));

            // Embrulhando a família em uma lista mutável
            user.setFamilies(new HashSet<>(List.of(family)));
        } else {
            // Inicializando com lista vazia em vez de null (evita NullPointerException)
            user.setFamilies(new HashSet<>());
        }

        // 2. Salva o Usuário
        user = userRepository.save(user);

        // 3. Se for MENOR, cria a Wallet automaticamente
        if (dto.getRole() == UserRole.MINOR) {
            Wallet wallet = new Wallet();
            wallet.setMinor(user); // Vínculo OneToOne
            wallet.setTokenBalances(0);
            wallet.setMoneyBalances(0.0);
            wallet.setHistoricalTokens(new ArrayList<>());
            wallet.setHistoricalMoney(new ArrayList<>());

            walletRepository.save(wallet);
        }

        return heroMapper.toUserDTO(user);
    }
}