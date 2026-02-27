package br.com.coretech.hero_api.users.services;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.users.enums.UserRole;
import br.com.coretech.hero_api.users.dtos.UserCreateDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private FamilyRepository familyRepository;
    private WalletRepository walletRepository;
    private HeroMapper heroMapper;
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
            user.setFamily(family);
        } else {
            user.setFamily(null); // Permite criar usuário sem família inicial
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