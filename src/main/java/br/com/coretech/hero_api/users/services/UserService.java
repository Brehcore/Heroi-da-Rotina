package br.com.coretech.hero_api.users.services;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.users.UserRole;
import br.com.coretech.hero_api.users.dtos.UserCreateDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.users.entities.Family;
import br.com.coretech.hero_api.users.entities.Usuario;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import br.com.coretech.hero_api.users.repositories.FamilyRepository;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FamilyRepository familyRepository;
    @Autowired
    private WalletRepository walletRepository; // Necessário para criar a carteira do menor
    @Autowired
    private HeroMapper heroMapper;

    @Transactional
    public UserResponseDTO criarUsuario(UserCreateDTO dto) {
        // 1. Busca ou cria família (Lógica simplificada: assume que ID da familia vem no DTO)
        Family family = familyRepository.findById(dto.getFamilyId())
                .orElseThrow(() -> new RuntimeException("Família não encontrada"));

        // 2. Cria Usuário
        Usuario usuario = new Usuario();
        usuario.setName(dto.getName());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword()); // TODO: Usar BCryptPasswordEncoder aqui em produção!
        usuario.setRole(dto.getRole());
        usuario.setFamily(family);

        usuario = userRepository.save(usuario);

        // 3. Se for MENOR, cria a Wallet automaticamente
        if (dto.getRole() == UserRole.MENOR) {
            Wallet wallet = new Wallet();
            wallet.setMenor(usuario); // Vínculo OneToOne
            wallet.setSaldoFichas(0);
            wallet.setSaldoDinheiro(0.0);
            wallet.setHistoricoFichas(new ArrayList<>());
            wallet.setHistoricoDinheiro(new ArrayList<>());

            walletRepository.save(wallet);
        }

        return heroMapper.toUsuarioDTO(usuario);
    }
}