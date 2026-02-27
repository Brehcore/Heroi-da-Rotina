package br.com.coretech.hero_api.users.controllers;

import br.com.coretech.hero_api.users.dtos.UserCreateDTO;
import br.com.coretech.hero_api.users.dtos.UserLoginDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import br.com.coretech.hero_api.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private UserService userService;
    private UserRepository userRepository;
    private HeroMapper heroMapper;

    // POST: Criar novo usuário (Monitor ou Menor)
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserCreateDTO dto) {
        UserResponseDTO newUsers = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUsers);
    }

    // GET: Buscar por Email (Simulação de Login simples ou checagem)
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> searchByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(heroMapper::toUserDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}