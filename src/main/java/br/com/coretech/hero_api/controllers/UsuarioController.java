package br.com.coretech.hero_api.controllers;

import br.com.coretech.hero_api.dtos.UsuarioCreateDTO;
import br.com.coretech.hero_api.dtos.UsuarioResponseDTO;
import br.com.coretech.hero_api.entities.Usuario;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.repositories.UsuarioRepository;
import br.com.coretech.hero_api.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*") // Libera acesso para o Angular
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HeroMapper heroMapper;

    // POST: Criar novo usuário (Monitor ou Menor)
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody UsuarioCreateDTO dto) {
        UsuarioResponseDTO novoUsuario = usuarioService.criarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    // GET: Buscar por Email (Simulação de Login simples ou checagem)
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorEmail(@PathVariable String email) {
        return usuarioRepository.findByEmail(email)
                .map(heroMapper::toUsuarioDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET: Listar todos os membros de uma família (Para o Dashboard do Monitor no Angular)
    @GetMapping("/familia/{familiaId}")
    public ResponseEntity<List<UsuarioResponseDTO>> listarFamilia(@PathVariable Long familiaId) {
        List<UsuarioResponseDTO> membros = usuarioRepository.findAllByFamiliaId(familiaId)
                .stream()
                .map(heroMapper::toUsuarioDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(membros);
    }
}