package br.com.coretech.hero_api.users.controllers;

import br.com.coretech.hero_api.users.dtos.UserCreateDTO;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import br.com.coretech.hero_api.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuário", description = "Gerenciamento de usuários na plataforma")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final HeroMapper heroMapper;

    // POST: Criar novo usuário (Monitor ou Menor)
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário (monitor ou menor)")
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserCreateDTO dto) {
        UserResponseDTO newUsers = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUsers);
    }

    // GET: Buscar por Email (Simulação de Login simples ou checagem)
    @Operation(summary = "Buscar e-mail", description = "Busca por e-mail (simulação de login simples ou checagem)")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> searchByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(heroMapper::toUserDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}