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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

    @Value("${application.security.jwt.cookie-secure:false}")
    private boolean cookieSecure;

    @Operation(summary = "Registrar usuário", description = "Registra um novo usuário Monitor")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegisterDTO dto) {
        UserResponseDTO newMonitor = authService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMonitor);
    }

    @Operation(summary = "Realiza login no sistema")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto, HttpServletResponse response) {
        LoginResponseDTO loginResponse = authService.login(dto);

        // Cria o cookie seguro HttpOnly
        ResponseCookie cookie = ResponseCookie.from("access_token", loginResponse.getToken())
                .httpOnly(true)
                .secure(cookieSecure) // false em desenvolvimento local (HTTP), true em produção (HTTPS)
                .path("/")
                .maxAge(86400) // 24 horas (mesmo tempo do token)
                .sameSite("Lax") // Proteção contra CSRF mantendo navegação padrão entre abas
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Retorna o DTO com dados do usuário (o token continua indo caso clientes externos usem, mas o Angular ignorará)
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Logout do sistema", description = "Invalida o cookie de autenticação")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Remove o cookie definindo maxAge como 0
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
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