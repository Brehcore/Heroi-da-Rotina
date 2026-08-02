package br.com.coretech.hero_api.users.controllers;

import br.com.coretech.hero_api.users.dtos.UserCreateDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuário", description = "Gerenciamento de usuários na plataforma")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Criar usuário", description = "Cria um novo usuário (monitor ou menor)")
    @PostMapping
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserCreateDTO dto) {
        UserResponseDTO newUsers = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUsers);
    }

    @Operation(summary = "Remove um usuário", description = "Remove um usuário da família")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MONITOR')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Meu perfil", description = "Retorna os dados do usuário autenticado no sistema")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        UserResponseDTO profile = userService.getAuthenticatedUserDTO(userEmail);
        return ResponseEntity.ok(profile);
    }

}