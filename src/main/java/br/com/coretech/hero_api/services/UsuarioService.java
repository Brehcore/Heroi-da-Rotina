package br.com.coretech.hero_api.services;

import br.com.coretech.hero_api.dtos.UsuarioCreateDTO;
import br.com.coretech.hero_api.dtos.UsuarioResponseDTO;
import br.com.coretech.hero_api.entities.Carteira;
import br.com.coretech.hero_api.entities.Familia;
import br.com.coretech.hero_api.entities.Usuario;
import br.com.coretech.hero_api.enums.RoleUsuario;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.repositories.CarteiraRepository;
import br.com.coretech.hero_api.repositories.FamiliaRepository;
import br.com.coretech.hero_api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private FamiliaRepository familiaRepository;
    @Autowired
    private CarteiraRepository carteiraRepository; // Necessário para criar a carteira do menor
    @Autowired
    private HeroMapper heroMapper;

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioCreateDTO dto) {
        // 1. Busca ou cria família (Lógica simplificada: assume que ID da familia vem no DTO)
        Familia familia = familiaRepository.findById(dto.getFamiliaId())
                .orElseThrow(() -> new RuntimeException("Família não encontrada"));

        // 2. Cria Usuário
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha()); // TODO: Usar BCryptPasswordEncoder aqui em produção!
        usuario.setRole(dto.getRole());
        usuario.setFamilia(familia);

        usuario = usuarioRepository.save(usuario);

        // 3. Se for MENOR, cria a Carteira automaticamente
        if (dto.getRole() == RoleUsuario.MENOR) {
            Carteira carteira = new Carteira();
            carteira.setMenor(usuario); // Vínculo OneToOne
            carteira.setSaldoFichas(0);
            carteira.setSaldoDinheiro(0.0);
            carteira.setHistoricoFichas(new ArrayList<>());
            carteira.setHistoricoDinheiro(new ArrayList<>());

            carteiraRepository.save(carteira);
        }

        return heroMapper.toUsuarioDTO(usuario);
    }
}