package br.com.coretech.hero_api.users.controllers;

import br.com.coretech.hero_api.users.dtos.ForgotPasswordDTO;
import br.com.coretech.hero_api.users.dtos.ResetPasswordDTO;
import br.com.coretech.hero_api.users.dtos.UserCreateDTO;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.users.dtos.UserRegisterDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.users.repositories.UserRepository;
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
    private final UserRepository userRepository;
    private final HeroMapper heroMapper;

    @Operation(summary = "Registrar usuário", description = "Registra um novo usuário Monitor")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegisterDTO dto) {
        UserResponseDTO newMonitor = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMonitor);
    }

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
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar e-mail", description = "Busca por e-mail (simulação de login simples ou checagem)")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> searchByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(heroMapper::toUserDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Solicitar reset", description = "Solicita o reset para enviar um token no e-mail")
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordDTO dto) {
        userService.forgotPassword(dto.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Trocar senha", description = "Reseta a senha e exclui o token")
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(ResetPasswordDTO dto) {
        userService.resetPasswordWithToken(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Meu perfil", description = "Retorna os dados do usuário autenticado no sistema")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        UserResponseDTO profile = userService.getAuthenticatedUserDTO(userEmail);
        return ResponseEntity.ok(profile);
    }

}