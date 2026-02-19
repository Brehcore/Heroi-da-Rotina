package br.com.coretech.hero_api.users.controllers;

import br.com.coretech.hero_api.users.dtos.UserCreateDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import br.com.coretech.hero_api.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*") // Libera acesso para o Angular
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HeroMapper heroMapper;

    // POST: Criar novo usuário (Monitor ou Menor)
    @PostMapping
    public ResponseEntity<UserResponseDTO> criar(@RequestBody UserCreateDTO dto) {
        UserResponseDTO novoUsuario = userService.criarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    // GET: Buscar por Email (Simulação de Login simples ou checagem)
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> buscarPorEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(heroMapper::toUsuarioDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET: Listar todos os membros de uma família (Para o Dashboard do Monitor no Angular)
    @GetMapping("/familia/{familiaId}")
    public ResponseEntity<List<UserResponseDTO>> listarFamilia(@PathVariable Long familiaId) {
        List<UserResponseDTO> membros = userRepository.findAllByFamiliaId(familiaId)
                .stream()
                .map(heroMapper::toUsuarioDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(membros);
    }
}