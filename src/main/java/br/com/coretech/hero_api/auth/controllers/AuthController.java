package br.com.coretech.hero_api.auth.controllers;

import br.com.coretech.hero_api.auth.dtos.ChangePasswordDTO;
import br.com.coretech.hero_api.auth.dtos.ForgotPasswordDTO;
import br.com.coretech.hero_api.auth.dtos.LoginResponseDTO;
import br.com.coretech.hero_api.auth.dtos.ResetPasswordDTO;
import br.com.coretech.hero_api.auth.services.AuthService;
import br.com.coretech.hero_api.auth.dtos.LoginRequestDTO;
import br.com.coretech.hero_api.users.dtos.UserRegisterDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Responsável pela autenticação do usuário no sistema")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar usuário", description = "Registra um novo usuário Monitor")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegisterDTO dto) {
        UserResponseDTO newMonitor = authService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMonitor);
    }

    @Operation(summary = "Realiza login no sistema")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Solicitar reset", description = "Solicita o reset para enviar um token no e-mail")
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordDTO dto) {
        authService.forgotPassword(dto.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Trocar senha", description = "Reseta a senha e exclui o token")
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordDTO dto) {
        authService.resetPasswordWithToken(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Mudar senha", description = "O usuário autenticado modifica a senha")
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordDTO dto, Authentication authentication) {
        String email = authentication.getName();
        authService.changePassword(dto, email);
        return ResponseEntity.ok().build();
    }
}
